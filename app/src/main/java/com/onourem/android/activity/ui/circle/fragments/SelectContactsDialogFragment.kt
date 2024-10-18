package com.onourem.android.activity.ui.circle.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.coroutinespermission.PermissionManager
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseDialogBindingFragment
import com.onourem.android.activity.databinding.DialogSelectContactsBinding
import com.onourem.android.activity.models.QualityQuestion
import com.onourem.android.activity.prefs.NameFormat
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.audio.fragments.openAppSystemSettings
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.circle.FriendCircleGameViewModel
import com.onourem.android.activity.ui.circle.adapters.MultiChoiceContactsAdapter
import com.onourem.android.activity.ui.circle.models.ContactItem
import com.onourem.android.activity.ui.circle.models.PhoneContact
import com.onourem.android.activity.ui.circle.models.QuestionForContacts
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.progress.OnouremProgressDialog
import contacts.async.findWithContext
import contacts.core.*
import contacts.core.entities.Contact
import contacts.core.entities.PhoneEntity
import contacts.core.entities.custom.CustomDataRegistry
import contacts.core.log.AndroidLogger
import contacts.core.util.lookupKeyIn
import contacts.core.util.phoneList
import contacts.entities.custom.gender.GenderRegistration
import contacts.entities.custom.googlecontacts.GoogleContactsRegistration
import contacts.entities.custom.handlename.HandleNameRegistration
import contacts.entities.custom.pokemon.PokemonRegistration
import contacts.entities.custom.rpg.RpgRegistration
import contacts.permissions.queryWithPermission
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.random.Random

class SelectContactsDialogFragment :
    AbstractBaseDialogBindingFragment<FriendCircleGameViewModel, DialogSelectContactsBinding>(),
    CoroutineScope by MainScope() {

    val contacts by lazy(LazyThreadSafetyMode.NONE) {
        Contacts(
            fragmentContext,
            customDataRegistry = CustomDataRegistry().register(
                GenderRegistration(),
                GoogleContactsRegistration(),
                HandleNameRegistration(),
                PokemonRegistration(),
                RpgRegistration()
            ),
            logger = AndroidLogger(redactMessages = !BuildConfig.DEBUG)
        )
    }

    private lateinit var onouremProgressDialog: OnouremProgressDialog
    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(parentJob + Dispatchers.Default)

    private var queryJob: Job? = null
    private var queryContactDetailsJob: Job? = null
    private val searchText: String
        get() = binding.edtSearchQuery.text.toString()
    private var contactsApiResult = emptyList<Contact>()

    private lateinit var questionList: ArrayList<QualityQuestion>

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    private var userActionViewModel: UserActionViewModel? = null
    private lateinit var contactsAdapter: MultiChoiceContactsAdapter
    private lateinit var contactList: ArrayList<ContactItem>
    private lateinit var selectedContactList: ArrayList<ContactItem>

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.isNotEmpty()) {
                contactsAdapter.filter.filter(s)
            } else {
                contactsAdapter.filter.filter("")
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private fun showContacts() {
        onouremProgressDialog.showDialogWithText(
            "Retrieving your contacts. Please wait for a few seconds.",
            false
        )
        queryJob?.cancel()
        queryJob = launch {
            // Using BroadQuery here so that it matches closely to the native Contacts app search
            // results. Consumers should try out Query too because it gives the most control.
            contactsApiResult = contacts
                .query()
                .include(
                    Fields.Contact.LookupKey,
                    when (preferenceHelper!!.nameFormat) {
                        NameFormat.FIRST_NAME_FIRST -> Fields.Contact.DisplayNamePrimary
                        NameFormat.LAST_NAME_FIRST -> Fields.Contact.DisplayNameAlt
                    }
                )
                .where { Phone.Number.isNotNullOrEmpty() }
                .orderBy(
                    ContactsFields.Options.Starred.desc(),
                    ContactsFields.DisplayNamePrimary.asc()
                )
                .findWithContext()

            setContactsAdapterItems()
        }
    }

    private fun setContactsAdapterItems() {

        contactsApiResult.map { it ->
            val displayName = when (preferenceHelper!!.nameFormat) {
                NameFormat.FIRST_NAME_FIRST -> it.displayNamePrimary
                NameFormat.LAST_NAME_FIRST -> it.displayNameAlt
            }

            val contactItem = ContactItem()
            contactItem.displayName = displayName
            contactItem.lookupKey = it.lookupKey
            contactItem.selected = false
            contactItem.isRowCreated = false
            contactItem.photoUrl = ""
            contactItem.selectedMobileNumber = ""
            val phoneList = ArrayList<PhoneContact>()

            it.phoneList().forEach {
                val phoneContact = PhoneContact()
                phoneContact.phone = it.number.toString()
                if (it.type != null) {
                    if (it.type == PhoneEntity.Type.CUSTOM) {
                        phoneContact.type = it.label!!.toString().uppercase()
                    } else {
                        phoneContact.type = it.type!!.name
                    }

                } else {
                    phoneContact.type = "MOBILE"
                }

                phoneContact.isPicked = false
                phoneList.add(phoneContact)
            }
            val distinct: ArrayList<PhoneContact> =
                phoneList.distinctBy { it.phone } as ArrayList<PhoneContact>
            contactItem.arrayListPhone = distinct
            contactList.add(contactItem)
        }

        onouremProgressDialog.hideDialog()
        binding.txtIntroSubTitle.text = "${0} Selected"

        (fragmentContext as DashboardActivity).setContactList(contactList)
        contactsAdapter.notifyDataSetChanged()
    }


    override fun layoutResource(): Int {
        return R.layout.dialog_select_contacts
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);

        setHasOptionsMenu(true)

        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            UserActionViewModel::class.java
        )
    }

//    override fun onPrepareOptionsMenu(menu: Menu) {
//        menu.findItem(R.id.search_nav).isVisible = false
//        menu.findItem(R.id.profile_nav).isVisible = false
//        menu.findItem(R.id.phone_nav).isVisible = false
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}
            override fun onPrepareMenu(menu: Menu) {
                menu.findItem(R.id.search_nav)?.setVisible(false)
                menu.findItem(R.id.profile_nav)?.setVisible(false)
                menu.findItem(R.id.phone_nav)?.setVisible(false)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        contactList = ArrayList()
        selectedContactList = ArrayList()
        questionList = ArrayList()

        onouremProgressDialog = OnouremProgressDialog(fragmentContext)

        val args = SelectContactsDialogFragmentArgs.fromBundle(requireArguments())

        binding.rvResult.layoutManager = LinearLayoutManager(
            requireActivity(),
            RecyclerView.VERTICAL,
            false
        )

        binding.parent.setOnClickListener(ViewClickListener {
            dismiss()
        })

        binding.btnSkip.setOnClickListener(ViewClickListener {
            dismiss()
        })

        binding.btnDone.setOnClickListener(ViewClickListener {

            if (contactsAdapter.selected.size >= 3) {

                selectedContactList = contactsAdapter.selected as ArrayList<ContactItem>

                viewModel.setSelectedContacts(selectedContactList.size.toString())

                questionList.addAll(args.questionListResponse.qualityQuestionList!!)

                questionList.forEachIndexed { index, questionItem ->
                    selectedContactList.forEach {
                        if (!it.isRowCreated) {
                            viewModel.insert(
                                QuestionForContacts(
                                    Random.nextInt().toString(),
                                    it.displayName!!,
                                    it.selectedMobileNumber,
                                    questionItem.questionId,
                                    false,
                                    index
                                )
                            )
                        }
                    }
                }

                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavFriendsCircleQuestionViewPager(
                        args.questionListResponse,
                        args.reload
                    )
                )
                dismiss()
            } else {
                showAlert("You need to have at least 3 friends selected to play the game")
            }

        })

        contactsAdapter = MultiChoiceContactsAdapter(contactList) { item ->

            getSingleContactDetails(item)
//            if (item.second.arrayListPhone.isEmpty()) {
//
//            }else{
//                if (item.first == MultiChoiceContactsAdapter.CLICK_SINGLE_CONTACT) { //single
//                    item.second.selected = !item.second.selected
//                    if (item.second.arrayListPhone.size > 0) {
//                        item.second.arrayListPhone[0].isPicked = true
//                        item.second.selectedMobileNumber = item.second.arrayListPhone[0].phone
//                        (fragmentContext as DashboardActivity).setContactList(contactList)
//                        contactsAdapter.notifyDataSetChanged()
//
//                        binding.txtIntroSubTitle.text =
//                            "${contactsAdapter.selected.size} Selected"
////                    contactsAdapter.notifyItemChanged(item.third, item.second)
//                    }
//                } else if (item.first == MultiChoiceContactsAdapter.CLICK_MULTIPLE_CONTACT) { //multiple
//
//                    if (!item.second.selected) {
//                        val titleText = "Please Select Any Number"
//                        val actions = java.util.ArrayList<Action<*>>()
//                        item.second.arrayListPhone.forEach {
//
//                            var numbers = ""
//                            numbers = if (it.type != "") {
//                                numbers.plus(String.format("%s: %s", it.type, it.phone))
//                            } else {
//                                numbers.plus(String.format("%s: %s", "Mobile", it.phone))
//                            }
//
//                            actions.add(
//                                Action(
//                                    numbers,
//                                    R.color.color_black,
//                                    ActionType.MULTIPLE_CONTACT,
//                                    Pair(it.phone, item)
//                                )
//                            )
//                        }
//                        val bundle = Bundle()
//                        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
//                        navController.navigate(
//                            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
//                                titleText,
//                                bundle, ""
//                            )
//                        )
//                    } else {
//                        item.second.selected = false
//                    }
//                    (fragmentContext as DashboardActivity).setContactList(contactList)
//                    contactsAdapter.notifyDataSetChanged()
//
//                }
//            }

        }
        binding.rvResult.adapter = contactsAdapter

        userActionViewModel!!.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel!!.actionConsumed()
                if (action.actionType == ActionType.MULTIPLE_CONTACT) {
                    val selectedNumberObject =
                        action.data as Pair<String, Triple<Int, ContactItem, Int>>
                    val phoneNumber = selectedNumberObject.first
                    val contactItem = selectedNumberObject.second
                    contactItem.second.selected = true
                    contactItem.second.arrayListPhone.forEach {
                        if (it.phone == phoneNumber) {
                            contactItem.second.selectedMobileNumber = phoneNumber
                            it.isPicked = true
                        }
                    }
//                    if (!contactItem.second.selected) {
//
//                        contactItem.second.selected = true
//                    } else {
//                        contactItem.second.selected = false
//                    }
                    //!contactItem.second.selected
                    binding.txtIntroSubTitle.text =
                        "${contactsAdapter.selected.size} Selected"
//                    contactsAdapter.notifyItemChanged(contactItem.third, contactItem.second)
                    (fragmentContext as DashboardActivity).setContactList(contactList)
                    contactsAdapter.notifyDataSetChanged()
                }

            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            coroutineScope.launch {
                withContext(Dispatchers.Main) {
                    handlePermissionResult(
                        PermissionManager.requestPermissions(
                            this@SelectContactsDialogFragment,
                            5,
                            Manifest.permission.READ_CONTACTS,
                        )
                    )
                }
            }
        } else {
            coroutineScope.launch {
                withContext(Dispatchers.Main) {
                    handlePermissionResult(
                        PermissionManager.requestPermissions(
                            this@SelectContactsDialogFragment,
                            5,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_CONTACTS,
                        )
                    )
                }
            }
        }


        binding.edtSearchQuery.addTextChangedListener(textWatcher)

    }

    private fun getSingleContactDetails(item: Triple<Int, ContactItem, Int>?) {
        queryContactDetailsJob?.cancel()
        queryContactDetailsJob = launch {
            val contact = contacts
                .queryWithPermission()
                .where { Contact.lookupKeyIn(item!!.second.lookupKey!!) }
                .findWithContext()
                .firstOrNull()
                ?.mutableCopy()

            if (contact != null && contact.hasPhoneNumber == true) {
                val phoneList = ArrayList<PhoneContact>()
                contact.phoneList().forEach {
                    val phoneContact = PhoneContact()
                    phoneContact.phone = it.number.toString()
                    if (it.type != null) {
                        if (it.type == PhoneEntity.Type.CUSTOM) {
                            phoneContact.type = it.label!!.toString().uppercase()
                        } else {
                            phoneContact.type = it.type!!.name
                        }

                    } else {
                        phoneContact.type = "MOBILE"
                    }

                    phoneContact.isPicked = false
                    phoneList.add(phoneContact)
                }

                val distinct: ArrayList<PhoneContact> =
                    phoneList.distinctBy { it.phone } as ArrayList<PhoneContact>
                item!!.second.arrayListPhone = distinct
                contactsAdapter.notifyDataSetChanged()
                if (item.second.arrayListPhone.size == 1) {
                    item.second.selected = !item.second.selected
                    item.second.arrayListPhone[0].isPicked = true
                    item.second.selectedMobileNumber = item.second.arrayListPhone[0].phone
                    (fragmentContext as DashboardActivity).setContactList(contactList)
                    contactsAdapter.notifyDataSetChanged()

                    binding.txtIntroSubTitle.text =
                        "${contactsAdapter.selected.size} Selected"
//                    contactsAdapter.notifyItemChanged(item.third, item.second)
                } else if (item.second.arrayListPhone.size > 1) {
                    if (!item.second.selected) {
                        val titleText = "Please Select Any Number"
                        val actions = java.util.ArrayList<Action<*>>()
                        item.second.arrayListPhone.forEach {

                            var numbers = ""
                            numbers = if (it.type != "") {
                                numbers.plus(String.format("%s: %s", it.type, it.phone))
                            } else {
                                numbers.plus(String.format("%s: %s", "Mobile", it.phone))
                            }

                            actions.add(
                                Action(
                                    numbers,
                                    R.color.color_black,
                                    ActionType.MULTIPLE_CONTACT,
                                    Pair(it.phone, item)
                                )
                            )
                        }
                        val bundle = Bundle()
                        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                                titleText,
                                bundle, ""
                            )
                        )
                    } else {
                        item.second.selected = false
                    }
                    (fragmentContext as DashboardActivity).setContactList(contactList)
                    contactsAdapter.notifyDataSetChanged()
                }
            }

        }
    }

    @SuppressLint("Range")
    fun getReadContacts(): ArrayList<ContactItem> {
        val contactList: ArrayList<ContactItem> = ArrayList()
        val cr: ContentResolver = fragmentContext.contentResolver
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val mainCursor: Cursor? =
            cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC "
            )
        if (mainCursor != null && mainCursor.count > 0) {
            while (mainCursor.moveToNext()) {

                if (mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) != null) {
                    val contactItem = ContactItem()
                    val mContactId: String =
                        mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val displayName: String =
                        mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))


                    val contactUri: Uri =
                        ContentUris.withAppendedId(
                            ContactsContract.Contacts.CONTENT_URI,
                            mContactId.toLong()
                        )
                    val displayPhotoUri: Uri =
                        Uri.withAppendedPath(
                            contactUri,
                            ContactsContract.Contacts.Photo.DISPLAY_PHOTO
                        )
//                  https://stackoverflow.com/questions/12562151/android-get-all-contacts

                    //ADD NAME AND CONTACT PHOTO DATA...
                    contactItem.displayName = displayName

                    contactItem.photoUrl = displayPhotoUri.toString()
//
                    val phones: Cursor = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " ='" + mContactId + "'",
                        null,
                        null
                    )!!

                    if (phones.count > 0) {
                        val arrayListPhone: ArrayList<PhoneContact> = ArrayList()
                        while (phones.moveToNext()) {
                            val phoneContact = PhoneContact()

                            val number =
                                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                            when (phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))) {
                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> phoneContact.type =
                                    "Home"

                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> phoneContact.type =
                                    "Mobile"

                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> phoneContact.type =
                                    "Work"
                            }

//                            val trimmedSpaceNumber = number.replace("\\s", "")
//                            val trimmedDashNumber = trimmedSpaceNumber.replace("-", "")
//                            val trimmedNumber = trimmedDashNumber.replace(" ", "")
//                            if (trimmedNumber.length >= 10) {
//                                val finalNumber =
//                                    trimmedNumber.substring(trimmedNumber.length - 10)
//
//                                phoneContact.phone = finalNumber
//                                phoneContact.isPicked = false
//                                arrayListPhone.add(phoneContact)
//                            }

                            phoneContact.phone = number
                            phoneContact.isPicked = false
                            arrayListPhone.add(phoneContact)
                        }
                        phones.close()
                        val distinct: ArrayList<PhoneContact> =
                            arrayListPhone.distinctBy { it.phone } as ArrayList<PhoneContact>

                        contactItem.arrayListPhone = distinct
                    }
                    contactList.add(contactItem)

                }

//                if (mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
//                        .toInt() > 0
//                ) {
//                    //ADD PHONE DATA...
//                    val arrayListPhone: ArrayList<PhoneContact> = ArrayList()
//                    val phoneCursor: Cursor? = cr.query(
//                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                        null,
//                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                        arrayOf(
//                            id
//                        ),
//                        null
//                    )
//                    if (phoneCursor != null) {
//                        while (phoneCursor.moveToNext()) {
//                            val phoneContact = PhoneContact()
//                            val phone: String =
//                                phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
//                            val trimmedSpaceNumber = phone.replace("\\s", "")
//                            val trimmedDashNumber = trimmedSpaceNumber.replace("-", "")
//                            val trimmedNumber = trimmedDashNumber.replace(" ", "")
//                            if (trimmedNumber.length >= 10) {
//                                val finalNumber =
//                                    trimmedNumber.substring(trimmedNumber.length - 10)
//
//                                phoneContact.phone = finalNumber
//                                phoneContact.isPicked = false
//                                arrayListPhone.add(phoneContact)
//
//                            }
//                        }
//                    }
//                    phoneCursor?.close()
//                    val distinct: ArrayList<PhoneContact> =
//                        arrayListPhone.distinctBy { it.phone } as ArrayList<PhoneContact>
//
//                    contactItem.arrayListPhone = distinct
//
//                }
//                contactList.add(contactItem)
            }
        }
        mainCursor?.close()
        return contactList
    }


    @SuppressLint("Range")
    fun getWhatsAppContacts(): ArrayList<ContactItem> {
        val contactList: ArrayList<ContactItem> = ArrayList()
        val cr: ContentResolver = fragmentContext.contentResolver


        //RowContacts for filter Account Types
        val contactCursor = cr.query(
            ContactsContract.RawContacts.CONTENT_URI, arrayOf(
                ContactsContract.RawContacts._ID,
                ContactsContract.RawContacts.CONTACT_ID
            ),
            ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?", arrayOf("com.whatsapp"),
            ContactsContract.Contacts.DISPLAY_NAME + " ASC "
        )

        val myWhatsappContacts: ArrayList<String> = ArrayList()

        if (contactCursor != null) {
            if (contactCursor.count > 0) {
                if (contactCursor.moveToFirst()) {
                    do {
                        //whatsappContactId for get Number,Name,Id ect... from  ContactsContract.CommonDataKinds.Phone
                        val whatsappContactId =
                            contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID))
                        if (whatsappContactId != null) {
                            //Get Data from ContactsContract.CommonDataKinds.Phone of Specific CONTACT_ID
                            val whatsAppContactCursor = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                arrayOf(
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                                ),
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                arrayOf(whatsappContactId),
                                null
                            )
                            if (whatsAppContactCursor != null) {
                                val contactItem = ContactItem()
                                val arrayListPhone: ArrayList<PhoneContact> = ArrayList()
                                whatsAppContactCursor.moveToFirst()
                                val id = whatsAppContactCursor.getString(
                                    whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                                )
                                val name = whatsAppContactCursor.getString(
                                    whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                                )
                                val number = whatsAppContactCursor.getString(
                                    whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                )
                                whatsAppContactCursor.close()

                                contactItem.displayName = name

                                val phoneContact = PhoneContact()

//                                when (whatsAppContactCursor.getInt(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))) {
//                                    ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> phoneContact.type =
//                                        "Home"
//                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> phoneContact.type =
//                                        "Mobile"
//                                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> phoneContact.type =
//                                        "Work"
//                                }

                                val trimmedSpaceNumber = number.replace("\\s", "")
                                val trimmedDashNumber = trimmedSpaceNumber.replace("-", "")
                                val trimmedNumber = trimmedDashNumber.replace(" ", "")
                                if (trimmedNumber.length >= 10) {
                                    val finalNumber =
                                        trimmedNumber.substring(trimmedNumber.length - 10)

                                    phoneContact.phone = finalNumber
                                    phoneContact.isPicked = false
                                    arrayListPhone.add(phoneContact)
                                }
                                contactItem.arrayListPhone = arrayListPhone

                                //Add Number to ArrayList
                                //myWhatsappContacts.add(number)

                                contactList.add(contactItem)
                            }
                        }
                    } while (contactCursor.moveToNext())
                    contactCursor.close()
                }
            }
        }

        val mainCursor: Cursor? =
            cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC "
            )
        if (mainCursor != null && mainCursor.count > 0) {
            while (mainCursor.moveToNext()) {

                if (mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) != null) {
                    val contactItem = ContactItem()
                    val mContactId: String =
                        mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val displayName: String =
                        mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))


                    val contactUri: Uri =
                        ContentUris.withAppendedId(
                            ContactsContract.Contacts.CONTENT_URI,
                            mContactId.toLong()
                        )
                    val displayPhotoUri: Uri =
                        Uri.withAppendedPath(
                            contactUri,
                            ContactsContract.Contacts.Photo.DISPLAY_PHOTO
                        )
//                  https://stackoverflow.com/questions/12562151/android-get-all-contacts

                    //ADD NAME AND CONTACT PHOTO DATA...
                    contactItem.displayName = displayName

                    contactItem.photoUrl = displayPhotoUri.toString()
//
                    val phones: Cursor = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " ='" + mContactId + "'",
                        null,
                        null
                    )!!

                    if (phones.count > 0) {
                        val arrayListPhone: ArrayList<PhoneContact> = ArrayList()
                        while (phones.moveToNext()) {
                            val phoneContact = PhoneContact()

                            val number =
                                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                            when (phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))) {
                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> phoneContact.type =
                                    "Home"

                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> phoneContact.type =
                                    "Mobile"

                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> phoneContact.type =
                                    "Work"
                            }

//                            val trimmedSpaceNumber = number.replace("\\s", "")
//                            val trimmedDashNumber = trimmedSpaceNumber.replace("-", "")
//                            val trimmedNumber = trimmedDashNumber.replace(" ", "")
//                            if (trimmedNumber.length >= 10) {
//                                val finalNumber =
//                                    trimmedNumber.substring(trimmedNumber.length - 10)

                            phoneContact.phone = number
                            phoneContact.isPicked = false
                            arrayListPhone.add(phoneContact)
//                            }
                        }
                        phones.close()
                        val distinct: ArrayList<PhoneContact> =
                            arrayListPhone.distinctBy { it.phone } as ArrayList<PhoneContact>

                        contactItem.arrayListPhone = distinct
                    }
                    contactList.add(contactItem)

                }

            }
        }
        mainCursor?.close()
        return contactList
    }


    override fun viewModelType(): Class<FriendCircleGameViewModel> {
        return FriendCircleGameViewModel::class.java
    }

    private fun handlePermissionResult(permissionResult: PermissionResult) {
        when (permissionResult) {
            is PermissionResult.PermissionGranted -> {

                val savedContactList =
                    (fragmentContext as DashboardActivity).getContactList()

                if (savedContactList != null && savedContactList.size > 0) {

                    val filteredList: ArrayList<ContactItem> =
                        savedContactList.filter { it.selected } as ArrayList<ContactItem>

                    filteredList.forEach {
                        if (it.selected) {
                            it.isRowCreated = true
                        }
                    }

                    val filteredListWithoutSelection: ArrayList<ContactItem> =
                        savedContactList.filter { !it.selected } as ArrayList<ContactItem>

                    contactList.addAll(filteredList.plus(filteredListWithoutSelection))

                    binding.txtIntroSubTitle.text = "${filteredList.size} Selected"

                    (fragmentContext as DashboardActivity).setContactList(contactList)
                    contactsAdapter.notifyDataSetChanged()
                } else {

                    showContacts()

//                    lifecycleScope.executeAsyncTask(onPreExecute = {
//                        onouremProgressDialog.showDialogWithText(
//                            "Retrieving your contacts. Please wait for a few seconds.",
//                            false
//                        )
//                    }, doInBackground = {
//                         // send data to "onPostExecute"
//                    }, onPostExecute = {
////                        // ... here "it" is a data returned from "doInBackground"
////                        val arrayList = it
////                        arrayList.forEach {
////                            if (it.arrayListPhone.size > 0) {
////                                contactList.add(it)
////                            }
////                        }
//
//
//                    })
                }

//                (fragmentContext as DashboardActivity).setContactList(contactList)
//                contactsAdapter.notifyDataSetChanged()

//                MainScope().launch {
//                    withContext(Dispatchers.Default) {
//                        //TODO("Background processing...")
//                        val savedContactList =
//                            (fragmentContext as DashboardActivity).getContactList()
//
//                        if (savedContactList != null && savedContactList.size > 0) {
//
//                            val filteredList: ArrayList<ContactItem> =
//                                savedContactList.filter { it.selected } as ArrayList<ContactItem>
//
//                            filteredList.forEach {
//                                if (it.selected) {
//                                    it.isRowCreated = true
//                                }
//                            }
//
//                            val filteredListWithoutSelection: ArrayList<ContactItem> =
//                                savedContactList.filter { !it.selected } as ArrayList<ContactItem>
//
//                            contactList.addAll(filteredList.plus(filteredListWithoutSelection))
//
//                            binding.txtIntroSubTitle.text =
//                                "${filteredList.size} Selected"
//                        } else {
//                            val arrayList = getReadContacts()
//                            arrayList.forEach {
//                                if (it.arrayListPhone.size > 0) {
//                                    contactList.add(it)
//                                }
//                            }
//
//                            binding.txtIntroSubTitle.text = "${0} Selected"
//                        }
//
//                    }
//                    //TODO("Update UI here!")
//                    onouremProgressDialog.hideDialog()
//                    (fragmentContext as DashboardActivity).setContactList(contactList)
//                    contactsAdapter.notifyDataSetChanged()
//
//                }
            }

            is PermissionResult.PermissionDenied -> {
                dismiss()
                Toast.makeText(fragmentContext, "Denied", Toast.LENGTH_SHORT).show()
            }

            is PermissionResult.ShowRational -> {
                showAlert(
                    "We need permissions to read contacts and access storage to work this Bond-003 Game.",
                    ViewClickListener {
                        when (permissionResult.requestCode) {

                            5 -> {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    coroutineScope.launch(Dispatchers.Main) {
                                        handlePermissionResult(
                                            PermissionManager.requestPermissions(
                                                this@SelectContactsDialogFragment,
                                                5,
                                                Manifest.permission.READ_CONTACTS,
                                            )
                                        )
                                    }
                                } else {
                                    coroutineScope.launch(Dispatchers.Main) {
                                        handlePermissionResult(
                                            PermissionManager.requestPermissions(
                                                this@SelectContactsDialogFragment,
                                                5,
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.READ_CONTACTS,
                                            )
                                        )
                                    }
                                }

                            }
                        }
                    },
                    "Ok",

                    )

            }

            is PermissionResult.PermissionDeniedPermanently -> {
                showAlert(
                    "You have denied app permissions permanently, We need permissions to read contacts and access storage to work this Bond-003 Game. Please go to Settings and give Onourem App required permissions to use the app.",
                    ViewClickListener {
                        dismiss()
                        fragmentContext.openAppSystemSettings()
                    })
            }
        }
    }

}