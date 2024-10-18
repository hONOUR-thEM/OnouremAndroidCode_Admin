package com.onourem.android.activity.ui.utils

import com.google.gson.Gson
import com.onourem.android.activity.R
import com.onourem.android.activity.models.GetUserMoodResponseMsgResponse
import com.onourem.android.activity.models.UserExpressionList
import com.onourem.android.activity.prefs.SharedPreferenceHelper

/**
 * Created by Kedar Labde on 12-July-2021
 * Onourem Social Games
 */
object LocalMoods {

    fun getAllMoodsMap(): HashMap<String, UserExpressionList> {
        val expressionMap: HashMap<String, UserExpressionList> = HashMap()

        expressionMap["106"] = UserExpressionList(
            "106",
            "Abandoned",
            "A sense of alienation and estrangement makes it very difficult to enjoy anything. Just know that you are unique, and not everyone can understand your feelings. Look around for things or people that make you comfortable. Keep Calm. Believe that nothing is permanent, and this feeling will disappear with time as you build new strong relationships.",
            R.drawable.lonely,
            "-4",
            "-3"
        )
        expressionMap["243"] = UserExpressionList(
            "243",
            "Accepted",
            "Be grateful that you are feeling this way. Enjoy the feeling and engage in wholesome activities that further enhance your experience. Talk to a friend you have not spoken to in a while. Do something kind for someone around you. Make someone feel warm.",
            R.drawable.relaxed,
            "3",
            "-1"
        )
        expressionMap["180"] = UserExpressionList(
            "180",
            "Afraid",
            "Take a few deep breaths. A friend may help you see this situation from a less stressful perspective. Talk and share if possible. It is difficult to think clearly when we are afraid. Pray.",
            R.drawable.scared,
            "-2",
            "3"
        )
        expressionMap["203"] = UserExpressionList(
            "203",
            "Aggressive",
            "Sometimes, it may seem like a good emotion, but it makes us less considerate. So take your time and let this aggression cool off before making an important decision. Go for a run or engage in any physically tiring activity to let the steam out.",
            R.drawable.angry,
            "-1",
            "3"
        )
        expressionMap["92"] = UserExpressionList(
            "92",
            "Alienated",
            "A sense of alienation and estrangement makes it very difficult to enjoy anything. Just know that you are unique, and not everyone can understand your feelings. Look around for things or people that make you comfortable. Keep Calm. Believe that nothing is permanent, and this feeling of alienation will disappear with time.",
            R.drawable.depressed,
            "-5",
            "-3"
        )
        expressionMap["278"] = UserExpressionList(
            "278",
            "Amazed",
            "One of those moments you will remember for life. Have fun! A small challenge for you. Close your eyes and observe how your breath feels around your nostrils for 5 minutes without getting distracted.",
            R.drawable.inspired,
            "5",
            "5"
        )
        expressionMap["54"] = UserExpressionList(
            "54",
            "Angry",
            "It is absolutely normal to feel angry towards someone or something. Use humor to release tension. Avoid taking any decisions as anger clouds our judgement. Let this pass. Once you're calm, express your concerns.",
            R.drawable.angry,
            "-3",
            "-3"
        )
        expressionMap["55"] = UserExpressionList(
            "55",
            "Annoyed",
            "Ignore people or things that are annoying you if you can. If you can't, try your best to keep your calm around them. Fun challenge: Observe your thoughts. Sometimes you will make interesting observations about your own mind. For example, you may find that your brain is getting worked up just because of its preferences. And that nobody is intentionally doing anything to annoy you.",
            R.drawable.annoyed,
            "-1",
            "2"
        )
        expressionMap["86"] = UserExpressionList(
            "86",
            "Anxious",
            "Take a few deep breaths. It is ok to feel anxious sometimes. A friend may help you see this situation from a less stressful perspective. Talk. Share. Just know that in many situations we get anxious about, their outcomes don't end up defining who we are in the long run. So focus on just giving your best. ",
            R.drawable.anxious,
            "-5",
            "2"
        )
        expressionMap["194"] = UserExpressionList(
            "194",
            "Apathetic",
            "A lot of people feel that way once in a while. If the feeling persists, try to understand the root cause. Is it because you are stressed? Or hurt? Or disappointed? Identify the reason behind the emotion and work on it.",
            R.drawable.guilty,
            "-1",
            "-1"
        )
        expressionMap["96"] = UserExpressionList(
            "96",
            "Appalled",
            "We hope you don't have to go through this emotion again. Sometimes we can do something, and sometimes we can't do anything to improve the situation. When you can't, pray! Call a friend and share how you are feeling. Engage in soulful activities like helping others and tending to plants.",
            R.drawable.irritated,
            "-5",
            "1"
        )
        expressionMap["118"] = UserExpressionList(
            "118",
            "Apprehensive",
            "Sometimes life presents situations that make us uncomfortable. Focus on what you can do to be best prepared for the upcoming situation. If you have a friend you trust, share your apprehensions. Sometimes we make things much bigger in our minds than they really are.",
            R.drawable.scared,
            "-4",
            "2"
        )
        expressionMap["230"] = UserExpressionList(
            "230",
            "Aroused",
            "Acknowledge them and set them aside for later. Put on some music, and exercise regularly. Make sure to stay safe in anything you do.",
            R.drawable.excited,
            "2",
            "2"
        )
        expressionMap["87"] = UserExpressionList(
            "87",
            "Ashamed",
            "We understand how it feels. But, most of the time, it is not as bad as it seems in our minds. Introspect to identify how you can act differently in the future.",
            R.drawable.ashamed,
            "-3",
            "2"
        )
        expressionMap["279"] = UserExpressionList(
            "279",
            "Astonished",
            "Hope you get to spend enough time thinking about what made you feel this way.  Compliment those who made you experience this.",
            R.drawable.inspired,
            "5",
            "5"
        )
        expressionMap["215"] = UserExpressionList(
            "215",
            "At ease",
            "Lovely! Enjoy the moment. If you feel like it, check on friends. Don't forget to note down on Onourem any specific incident that has made you feel this way. ",
            R.drawable.relaxed,
            "1",
            "-1"
        )
        expressionMap["280"] = UserExpressionList(
            "280",
            "Awe",
            "Hope you get to spend enough time thinking about what made you feel this way.  Compliment those who made you experience this.",
            R.drawable.inspired,
            "5",
            "5"
        )
        expressionMap["107"] = UserExpressionList(
            "107",
            "Awful",
            "Set aside some quiet time for yourself. Mentally open the door to guilt, frustration, regret, anger, and any other emotions that might come up. Writing down what you feel can help.",
            R.drawable.depressed,
            "-4",
            "-3"
        )
        expressionMap["171"] = UserExpressionList(
            "171",
            "Bad",
            "Some days are like that. Breathe through this emotion. Your mind will stop focussing on this subject in some time, and it won't feel this bad. Or you can try to intentionally focus your mind on other activities that make you feel better. Talk to a friend and share how you are feeling. If you are up for a little challenge, try observing your thoughts when you are in a bad mood. This will tell you a lot about your own mind. How it reacts to situations.",
            R.drawable.lonely,
            "-2",
            "-1"
        )
        expressionMap["270"] = UserExpressionList(
            "270",
            "Balanced",
            "A wonderful way to be. Enjoy whatever you are doing. If you feel like it, talk to a friend you have not spoken to in a while. Find out if they are doing well and, if not, what you can do to make them feel better.",
            R.drawable.relaxed,
            "5",
            "-3"
        )
        expressionMap["165"] = UserExpressionList(
            "165",
            "Betrayed",
            "Sorry, you have to go through this emotion. Allow yourself the time to get over it. Observe your thoughts to ensure your brain isn't encouraging negative feelings or actions. When possible, learn, forgive and move on. If you wish to challenge yourself, try doing something nice for people around you to pull yourself out of negativity.",
            R.drawable.sad,
            "-2",
            "-3"
        )
        expressionMap["153"] = UserExpressionList(
            "153",
            "Bitter",
            "Everyone feels this way once in a while. Take some time to understand the root cause of what makes you feel this way and see if there are better ways to look at things. Share how you are feeling with a friend.",
            R.drawable.angry,
            "-3",
            "3"
        )
        expressionMap["258"] = UserExpressionList(
            "258",
            "Blessed",
            "One of the best feelings. Use this moment to feel gratitude towards all those who have contributed to your life. If you have the energy, give some of them a call and appreciate them.",
            R.drawable.satisfied,
            "4",
            "-3"
        )
        expressionMap["273"] = UserExpressionList(
            "273",
            "Blissful",
            "Many people have probably not experienced this emotion in a long time. You are fortunate. Be grateful for all those who contributed to this journey. If you feel like it, check on your friends to ensure they are doing fine. Your positive energy may help them feel better. ",
            R.drawable.happy,
            "5",
            "1"
        )
        expressionMap["74"] = UserExpressionList(
            "74",
            "Bored",
            "A very common feeling. Pick a new hobby or interact with your loved ones. Go out or watch a movie, dance or sleep or do whatever you want. Just don't trouble your mother. A fun challenge: Close your eyes for 5 minutes. Try to observe the sensations air creates in your nostrils as you breathe normally. You will realize how difficult it is to focus your brain on something. Your brain will keep losing track of the sensations and start thinking about something else. Read about meditation and practice it when possible if you find this interesting.",
            R.drawable.bored,
            "-1",
            "-2"
        )
        expressionMap["195"] = UserExpressionList(
            "195",
            "Busy",
            "Well, we hope you are enjoying being busy. If not, you get a break soon to unwind. Talk to a friend before going to bed.",
            R.drawable.anxious,
            "-1",
            "-1"
        )
        expressionMap["214"] = UserExpressionList(
            "214",
            "Calm",
            "We hope you can maintain this calm for as long as possible. If you feel like it, talk to a friend you haven't spoken to in a long time. Note down what made you feel calm if you have not already. This will come in handy in the future when you aren't feeling well.",
            R.drawable.relaxed,
            "1",
            "-2"
        )
        expressionMap["269"] = UserExpressionList(
            "269",
            "Carefree",
            "Enjoy the moment. We wish you get to feel this emotion often while being responsible. You can enhance this experience by thinking of all the people who have contributed to your life. Feel grateful to them and if possible, give them a call to express your appreciation.",
            R.drawable.relaxed,
            "5",
            "-4"
        )
        expressionMap["233"] = UserExpressionList(
            "233",
            "Cheeky",
            "Close friends and family are perfect targets. Play an innocent prank on one of them. Maybe send them an appreciation message anonymously. Tell them a little later it was from you.",
            R.drawable.happy,
            "2",
            "3"
        )
        expressionMap["56"] = UserExpressionList(
            "56",
            "Cheerful",
            "Great. See if you can make a friend smile today. Journal the reasons behind this feeling. Reading these later will lift your spirits.",
            R.drawable.cheerful,
            "2",
            "4"
        )
        expressionMap["225"] = UserExpressionList(
            "225",
            "Chill",
            "Great. Have fun! If it is convenient, talk to a friend you have not spoken to in some time.",
            R.drawable.naughty,
            "2",
            "-3"
        )
        expressionMap["257"] = UserExpressionList(
            "257",
            "Comfy",
            "Glad you are feeling comfortable. Enjoy. Close your eyes and think of all the people who have contributed to your life. Express gratitude to them in your heart. If you feel like it, give some of them a call and say Thank You to them.",
            R.drawable.satisfied,
            "4",
            "-4"
        )
        expressionMap["223"] = UserExpressionList(
            "223",
            "Complacent",
            "It is great to feel satisfied in life. Not many people get to feel this emotion. While you are at peace, keep thinking about how to improve yourself.",
            R.drawable.satisfied,
            "2",
            "-5"
        )
        expressionMap["142"] = UserExpressionList(
            "142",
            "Concerned",
            "An emotion that keeps us vigilant to take the required action in an evolving situation. If you can't do anything, Pray! Provide your emotional support to those who may need it.",
            R.drawable.tense,
            "-3",
            "1"
        )
        expressionMap["249"] = UserExpressionList(
            "249",
            "Confident",
            "Such a great feeling! Make sure to not let your ego grow. Stay grounded, be kind and compassionate. Use this positive mindset to build wholesome habits. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better.",
            R.drawable.energetic,
            "3",
            "2"
        )
        expressionMap["57"] = UserExpressionList(
            "57",
            "Confused",
            "Lucky you! You have got options. A lot of people don't. If you are tired of thinking and this isn't urgent, maybe sleep on it. And take a decision. Sometimes, talking to a friend helps.",
            R.drawable.confused,
            "-1",
            "1"
        )
        expressionMap["244"] = UserExpressionList(
            "244",
            "Content",
            "Be grateful that you are feeling this way. Enjoy the feeling and engage in wholesome activities that further enhance your experience. Talk to a friend you have not spoken to in a while. Do something kind for someone around you. Make someone feel warm.",
            R.drawable.relaxed,
            "3",
            "-1"
        )
        expressionMap["253"] = UserExpressionList(
            "253",
            "Courageous",
            "That must feel good. Wish you all the best in your goals. Make sure to stay grounded, kind and compassionate.",
            R.drawable.energetic,
            "3",
            "4"
        )
        expressionMap["256"] = UserExpressionList(
            "256",
            "Cozy",
            "Enjoy this time. If you feel like it, talk to a friend you have not spoken to in a while. Otherwise, a fun challenge: Close your eyes for 5 minutes. Try to observe the sensations air creates in your nostrils as you breathe normally. You will realize how difficult it is to focus your brain on something. Your brain will keep losing track of the sensations and start thinking about something else. Read about meditation and practice it when possible if you find this interesting.",
            R.drawable.relaxed,
            "4",
            "-5"
        )
        expressionMap["251"] = UserExpressionList(
            "251",
            "Creative",
            "A good mood to be in. Show us your creativity in your interactions on Onourem. :-)",
            R.drawable.naughty,
            "3",
            "3"
        )
        expressionMap["143"] = UserExpressionList(
            "143",
            "Critical",
            "An emotion that serves well in some situations and not so well in others. If you really need to be critical right now, try being compassionate and kind at the same time.",
            R.drawable.embarrassed,
            "-3",
            "1"
        )
        expressionMap["218"] = UserExpressionList(
            "218",
            "Curious",
            ":-) Hopefully, your curiosity is positive and constructive. If you are curious to find out facts others may have not shared with you yet, be patient. ",
            R.drawable.naughty,
            "1",
            "3"
        )
        expressionMap["73"] = UserExpressionList(
            "73",
            "Depressed",
            "We care for you, and so do your friends and family, even if they have not expressed it lately. When you feel comfortable, share how you feel with someone you trust or reach out to a support group. If this phase continues, talking to a therapist may be the best thing to do. Meanwhile, invest your energy in soulful activities like helping the needy or teaching someone something. Try new things.",
            R.drawable.depressed,
            "-4",
            "-4"
        )
        expressionMap["125"] = UserExpressionList(
            "125",
            "Desolate",
            "A difficult emotion to go through. But, like all phases of life, this will pass too. Acknowledge the feeling, and cry if you feel like it. If possible, engage in activities that touch you at a deeper level. Talk to a friend who understands you, do something for the needy, or engage in an old hobby.",
            R.drawable.sad,
            "-4",
            "-5"
        )
        expressionMap["89"] = UserExpressionList(
            "89",
            "Despair",
            "Feeling defeated or losing hope? Keep breathing. Acknowledge your emotion instead of hiding or avoiding it. It may seem impossible right now, but even this stage of life will pass.",
            R.drawable.depressed,
            "-5",
            "-5"
        )
        expressionMap["90"] = UserExpressionList(
            "90",
            "Despondent",
            "Expecting no improvement? This might very well be the most challenging emotion to deal with. When you feel comfortable, share how you feel with someone you trust. ",
            R.drawable.depressed,
            "-5",
            "-4"
        )
        expressionMap["79"] = UserExpressionList(
            "79",
            "Disappointed",
            "Not a pleasant emotion. We hope you can put your attention on other things around you. Hopes and expectations have both pros and cons. When we have them, we keep looking forward to something. But if they aren't met, we feel bad. If possible, avoid letting this incident overshadow previous positive experiences.",
            R.drawable.disappointed,
            "-3",
            "-1"
        )
        expressionMap["136"] = UserExpressionList(
            "136",
            "Disapproving",
            "Glad you have noticed that you are feeling this way. Try that your emotion doesn't influence your decisions and makes them unfair to others around you.",
            R.drawable.tense,
            "-3",
            "-1"
        )
        expressionMap["131"] = UserExpressionList(
            "131",
            "Discouraged",
            "It's definitely not easy to deal with such a feeling. But remember that something or someone that kept you going till now. Feel proud of how far you've come. The upside of discouragement is it may motivate you to evaluate yourself. Reinterpret your setbacks as learning experiences and stepping stones.",
            R.drawable.disappointed,
            "-3",
            "-2"
        )
        expressionMap["94"] = UserExpressionList(
            "94",
            "Disgusted",
            "Breathe. Pay attention to what really triggered the emotion and your reaction. Think of how you would like to manage situations that may trigger this emotion again.",
            R.drawable.insecure,
            "-5",
            "-1"
        )
        expressionMap["166"] = UserExpressionList(
            "166",
            "Disheartened",
            "Sorry, you have to go through this emotion. Allow yourself the time to get over it. Observe your thoughts to ensure your brain isn't encouraging negative feelings or actions. When possible, learn, forgive and move on. If you wish to challenge yourself, try doing something nice for people around you to pull yourself out of negativity.",
            R.drawable.sad,
            "-2",
            "-3"
        )
        expressionMap["137"] = UserExpressionList(
            "137",
            "Disillusioned",
            "Hopefully, it is for the best that our expectations have been reset. Resist bitterness. Introspect the sequence of events that eventually led to this emotion. Identify any adjustments we need to make in our thinking process.",
            R.drawable.lonely,
            "-3",
            "-1"
        )
        expressionMap["206"] = UserExpressionList(
            "206",
            "Dismayed",
            "Breathe. Take your time to let this phase pass. Take time to understand the incident better. Respond if you need to when you are calm. What will matter in the long run is how you respond to unexpected situations. Talking to a friend may help get a different perspective.",
            R.drawable.restless,
            "-1",
            "5"
        )
        expressionMap["144"] = UserExpressionList(
            "144",
            "Dismissive",
            "Kudos! Most people don't realize when they are in a subtly bad mood. Everything else is easy. Guide your thoughts through the lens of compassion. Try to be fair and not let your mood influence your decisions.",
            R.drawable.worthless,
            "-3",
            "1"
        )
        expressionMap["138"] = UserExpressionList(
            "138",
            "Disrespected",
            "Not a pleasant emotion. Introspect to identify if any of your actions may have invoked a disrespectful reaction from others. It is not a justification for disrespecting anyone, but it is best to first focus on our actions. Hopefully, the incident doesn't damage your own thought process by invoking the desire to hurt others. Be the bigger human being. Introspect, forgive and move on.",
            R.drawable.lonely,
            "-3",
            "-1"
        )
        expressionMap["196"] = UserExpressionList(
            "196",
            "Distant",
            "A lot of people feel that way once in a while. If the feeling persists, try to understand the root cause. Is it because you are stressed? Or hurt? Or disappointed? Identify the reason behind the feeling and work on it. Talk to a close friend.",
            R.drawable.embarrassed,
            "-1",
            "-1"
        )
        expressionMap["209"] = UserExpressionList(
            "209",
            "Don't Know",
            "That is alright! Answer some questions, listen to some Vocals, or send some appreciation messages on Onourem. We are sure you will feel good.",
            R.drawable.confused,
            "0",
            "0"
        )
        expressionMap["172"] = UserExpressionList(
            "172",
            "Down",
            "Some days are like that. Breathe through this emotion. Your mind will stop focussing on this subject in some time, and it won't feel this bad. Or you can try to intentionally focus your mind on other activities that make you feel better. Talk to a friend and share how you are feeling. If you are up for a little challenge, try observing your thoughts when you are in a bad mood. This will tell you a lot about your own mind. How it reacts to situations.",
            R.drawable.lonely,
            "-2",
            "-1"
        )
        expressionMap["187"] = UserExpressionList(
            "187",
            "Drained",
            "Kudos to all the efforts you made. Take a break if you can. If not, know that this hard work will make you feel satisfied once this is done. Breathe. Keep your calm and do what needs to be done. Make sure to rejuvenate as soon as you can.",
            R.drawable.tired,
            "-1",
            "-5"
        )
        expressionMap["275"] = UserExpressionList(
            "275",
            "Eager",
            "Happy that you are looking forward to something. Sometimes we lose control of our actions in excitement. Hopefully, you can avoid that while having fun.",
            R.drawable.cheerful,
            "5",
            "3"
        )
        expressionMap["227"] = UserExpressionList(
            "227",
            "Easygoing",
            "A wonderful way to be. Enjoy whatever you are doing. If you feel like it, talk to a friend you have not spoken to in a while. Find out if they are doing well and, if not, what you can do to make them feel better.",
            R.drawable.cheerful,
            "2",
            "-1"
        )
        expressionMap["281"] = UserExpressionList(
            "281",
            "Ecstatic",
            "Hope you get to spend enough time thinking about what made you feel this way.  Compliment those who made you experience this.",
            R.drawable.cheerful,
            "5",
            "5"
        )
        expressionMap["277"] = UserExpressionList(
            "277",
            "Elated",
            "One of those moments you will remember for life. Have fun! A small challenge for you. Close your eyes and observe how your breath feels around your nostrils for 5 minutes without getting distracted.",
            R.drawable.energetic,
            "5",
            "4"
        )
        expressionMap["81"] = UserExpressionList(
            "81",
            "Embarrassed",
            "We understand how it feels. But, most of the time, it is not as bad as it seems in our minds. Introspect to identify how you can act differently in the future.",
            R.drawable.embarrassed,
            "-3",
            "2"
        )
        expressionMap["105"] = UserExpressionList(
            "105",
            "Empty",
            "Gently acknowledge the emptiness. If you recognize that your feelings are linked to a loss you experienced, consider allowing yourself time and space to grieve openly. Grief looks and feels different to everyone, and there are no right or wrong ways to do it.",
            R.drawable.lonely,
            "-4",
            "-4"
        )
        expressionMap["88"] = UserExpressionList(
            "88",
            "Energetic",
            "Great. See if you can bring a smile to a friend's face today. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better.",
            R.drawable.energetic,
            "1",
            "3"
        )
        expressionMap["102"] = UserExpressionList(
            "102",
            "Enraged",
            "Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. When you are calmer, don't forget to introspect if you would like your brain to react differently in the future.",
            R.drawable.irritated,
            "-5",
            "5"
        )
        expressionMap["252"] = UserExpressionList(
            "252",
            "Enthusiastic",
            "A wonderful way to be. Hope your enthusiasm rubs off on others around you. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better. Journal the reasons behind why you are feeling this way. It will help you later reading it.",
            R.drawable.excited,
            "3",
            "3"
        )
        expressionMap["58"] = UserExpressionList(
            "58",
            "Excited",
            "Best wishes for your plans and goals. Have fun! A small challenge for you. Close your eyes and observe how your breath feels around your nostrils for 5 minutes without getting distracted.",
            R.drawable.excited,
            "5",
            "3"
        )
        expressionMap["127"] = UserExpressionList(
            "127",
            "Excluded",
            "Take your time to understand the reasons behind your feelings, learn from them and move on. Look around for things or people that make you comfortable. ",
            R.drawable.lonely,
            "-4",
            "-3"
        )
        expressionMap["164"] = UserExpressionList(
            "164",
            "Exhausted",
            "Kudos to all the efforts you made. Take a break if you can. If not, know that this hard work will make you feel satisfied once this is done. Breathe. Keep your calm and do what needs to be done. Make sure to rejuvenate as soon as you can.",
            R.drawable.tired,
            "-2",
            "-4"
        )
        expressionMap["267"] = UserExpressionList(
            "267",
            "Exhilarated",
            "You must be a source of positive energy right now. Have fun. Dance if you like. Call a close friend and share your energy. Journal your thoughts. You will find them very handy to read later when you feel low.",
            R.drawable.cheerful,
            "4",
            "5"
        )
        expressionMap["149"] = UserExpressionList(
            "149",
            "Exposed",
            "We understand how it feels. But, most of the time, it is not as bad as it seems in our minds. Introspect to identify how you can act differently in the future.",
            R.drawable.embarrassed,
            "-3",
            "2"
        )
        expressionMap["188"] = UserExpressionList(
            "188",
            "Fatigued",
            "Kudos to all the efforts you made. Take a break if you can. If not, know that this hard work will make you feel satisfied once this is done. Breathe. Keep your calm and do what needs to be done. Make sure to rejuvenate as soon as you can.",
            R.drawable.tired,
            "-1",
            "-4"
        )
        expressionMap["181"] = UserExpressionList(
            "181",
            "Fearful",
            "Take a few deep breaths. A friend may help you see this situation from a less stressful perspective. Talk and share if possible. It is difficult to think clearly when we are afraid. Pray.",
            R.drawable.scared,
            "-2",
            "3"
        )
        expressionMap["255"] = UserExpressionList(
            "255",
            "Festive",
            "Enjoy your time. If you have a few minutes, call a friend you have not spoken to in a while.",
            R.drawable.cheerful,
            "3",
            "5"
        )
        expressionMap["59"] = UserExpressionList(
            "59",
            "Flirty",
            "Hmm. Go, flirt with your partner, ONLY. If you are single, stay decent and remember no means no.",
            R.drawable.flirting,
            "2",
            "2"
        )
        expressionMap["250"] = UserExpressionList(
            "250",
            "Focused",
            "Continue what you have been doing. Stay grounded, and be kind and compassionate. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better.",
            R.drawable.relaxed,
            "3",
            "2"
        )
        expressionMap["168"] = UserExpressionList(
            "168",
            "Fragile",
            "Everyone feels like this one time or the other. If you are feeling too weak, just breathe. Like all phases of life, this will pass as well. When you have the energy, observe your thoughts to identify what you need to feel less fragile. Talk to a friend.",
            R.drawable.confused,
            "-2",
            "-2"
        )
        expressionMap["245"] = UserExpressionList(
            "245",
            "Free",
            "Be grateful that you are feeling this way. Enjoy the feeling and engage in wholesome activities that further enhance your experience. Talk to a friend you have not spoken to in a while. Do something kind for someone around you. Make someone feel warm.",
            R.drawable.satisfied,
            "3",
            "-1"
        )
        expressionMap["121"] = UserExpressionList(
            "121",
            "Frightened",
            "Share how you are feeling with your friends. You may feel less frightened knowing you can rely on them in difficult situations.",
            R.drawable.scared,
            "-4",
            "3"
        )
        expressionMap["60"] = UserExpressionList(
            "60",
            "Frustrated",
            "Everyone faces this emotion. Observe your thoughts. Sometimes you may find that the reasons behind your frustrations are real. In those cases, think of what you can do constructively. But sometimes, no one is doing anything intentionally to trouble you. It is just that your brain has got too comfortable with its own preferences and is getting worked up when these preferences are not met. In these cases, work on your own thought patterns. Remember, every individual is different. Be patient, compassionate, and kind.",
            R.drawable.frustrated,
            "-3",
            "4"
        )
        expressionMap["272"] = UserExpressionList(
            "272",
            "Fulfilled",
            "Enjoy the moment. We wish you get to feel this emotion often. You can enhance this experience by thinking of all the people who have contributed to your life. Feel grateful to them. If you feel like it, tell them you appreciate them.",
            R.drawable.relaxed,
            "5",
            "-1"
        )
        expressionMap["100"] = UserExpressionList(
            "100",
            "Fuming",
            "Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. Later, don't forget to introspect if you would like your brain to react differently in the future. ",
            R.drawable.annoyed,
            "-5",
            "3"
        )
        expressionMap["122"] = UserExpressionList(
            "122",
            "Furious",
            "Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. When you are calmer, don't forget to introspect if you would like your brain to react differently in such a situation. ",
            R.drawable.angry,
            "-4",
            "4"
        )
        expressionMap["114"] = UserExpressionList(
            "114",
            "Glum",
            "A difficult emotion to go through. Talking to people with whom you feel a deeper connection may help. Go to bed and wake up at the same time every day. Keeping some routine will make you feel more structured and in control. Helping others can also help you feel a greater sense of connection and purpose. ",
            R.drawable.sad,
            "-4",
            "-1"
        )
        expressionMap["259"] = UserExpressionList(
            "259",
            "Grateful",
            "Gratitude brings so many benefits to those who feel it. Use this moment to appreciate all those who have contributed to your life. If you feel like it, give some of them a call and say Thank You.",
            R.drawable.relaxed,
            "4",
            "-2"
        )
        expressionMap["91"] = UserExpressionList(
            "91",
            "Grief",
            "It hurts so bad to lose someone or something that meant a lot to us. We understand. No words of wisdom can ever make us feel better in grief. But remember, it is important to allow yourself to express these feelings. Share with those who may have experienced a similar loss. Pray. Breathe.",
            R.drawable.depressed,
            "-5",
            "-4"
        )
        expressionMap["61"] = UserExpressionList(
            "61",
            "Guilty",
            "The fact that you are feeling guilty speaks volumes about the positivity in you. Set aside some quiet time for yourself. Mentally open the door to guilt, sit with those feelings and explore them with curiosity instead of judgment. If you wish to do something to improve the situation, do it. Else learn from the incident and move on.",
            R.drawable.guilty,
            "-3",
            "-2"
        )
        expressionMap["62"] = UserExpressionList(
            "62",
            "Happy",
            "Great. See if you can bring a smile to a friend's face today. Express gratitude in your heart to those who made you happy. Journal what made you feel this way. You will be amazed to see how it impacts you positively later when your spirits are down.",
            R.drawable.happy,
            "2",
            "2"
        )
        expressionMap["108"] = UserExpressionList(
            "108",
            "Helpless",
            "Sometimes you can do something, and sometimes you can't. When you can't, Pray! Focus on what you can control. Engage in meaningful activities to stay hopeful and provide emotional support to people around you.",
            R.drawable.depressed,
            "-4",
            "-3"
        )
        expressionMap["173"] = UserExpressionList(
            "173",
            "Hesitant",
            "You are hesitant. That also means you are careful, which is good. Sometimes hesitations are valid, and one should think clearly before taking a step. But sometimes, we just overthink how others may take it. Talk to a friend to take a second opinion if you aren't able to make up your mind.",
            R.drawable.confused,
            "-2",
            "1"
        )
        expressionMap["246"] = UserExpressionList(
            "246",
            "Hopeful",
            "Seeing you hopeful makes us happy. Use this positive mindset to build healthy habits and a good routine if possible. Plan for what you wish to achieve in the future and start working towards it peacefully. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better.",
            R.drawable.naughty,
            "3",
            "1"
        )
        expressionMap["103"] = UserExpressionList(
            "103",
            "Hopeless",
            "One of the toughest phases of life. But like all phases, this will pass too. Be patient. While this continues, engage in meaningful activities that give you satisfaction. Talk to friends who can understand your pain or read books that put life's phases in perspective.",
            R.drawable.depressed,
            "-4",
            "-5"
        )
        expressionMap["97"] = UserExpressionList(
            "97",
            "Horrified",
            "One of those moments when life presents an unpleasant and shocking situation. Know that this emotion may hamper your ability to think clearly, so avoid reacting to this situation at this moment if possible. Sleep on it. When you are calmer, think of how you can contribute constructively. ",
            R.drawable.irritated,
            "-5",
            "1"
        )
        expressionMap["176"] = UserExpressionList(
            "176",
            "Hostile",
            "Glad you recognize when your mood isn't right. When you know you are hostile, you can avoid making decisions that may impact others negatively. When you are calm, express your concerns. If possible, introspect so that you can handle similar situations with calm in the future.",
            R.drawable.angry,
            "-2",
            "2"
        )
        expressionMap["119"] = UserExpressionList(
            "119",
            "Humiliated",
            "No matter how much we all want to avoid this emotion, most people go through this a few times in their lives. If you made a mistake, be the confident one, own it, learn from it, and move on. Share how you are feeling with a friend. ",
            R.drawable.lonely,
            "-4",
            "2"
        )
        expressionMap["139"] = UserExpressionList(
            "139",
            "Hurt",
            "Not a pleasant emotion. Introspect to identify if any of your actions may have invoked a hurtful reaction from others. It is not a justification for hurting anyone, but it is best to first focus on our actions. Hopefully, the incident doesn't damage your own thought process by invoking the desire to hurt others. Be the bigger human being. Introspect, forgive and move on.",
            R.drawable.sad,
            "-3",
            "-1"
        )
        expressionMap["221"] = UserExpressionList(
            "221",
            "Hyper",
            "So good to have some extra energy. A lot of people will envy you for this. Hopefully, you can utilize it in constructive activities. For example, an exhaustive workout session will make you feel great.",
            R.drawable.surprised,
            "1",
            "4"
        )
        expressionMap["115"] = UserExpressionList(
            "115",
            "Inadequate",
            "We’re really sorry that you feel this way! We know what it feels like to always want to do more and be better. BE NICE TO YOURSELF! You deserve to feel loved, especially by you! You’re an amazing person who has done incredible things in your life. Be kind to yourself.",
            R.drawable.worthless,
            "-4",
            "-1"
        )
        expressionMap["197"] = UserExpressionList(
            "197",
            "Indifferent",
            "A lot of people feel that way once in a while. If the feeling persists, try to understand the root cause. Is it because you are stressed? Or hurt? Or disappointed? Identify the reason behind the feeling and work on it. Talk to a close friend.",
            R.drawable.embarrassed,
            "-1",
            "-1"
        )
        expressionMap["154"] = UserExpressionList(
            "154",
            "Indignant",
            "Unfair and unjust actions of others can make us angry sometimes. Take your time to observe why you are feeling this way. See if you can do something constructive to improve the situation. Don't let the emotion take you any decisions that you may regret later.",
            R.drawable.angry,
            "-3",
            "3"
        )
        expressionMap["116"] = UserExpressionList(
            "116",
            "Inferior",
            "We understand that it must be hard on you. We all experience it from time to time, especially if we're in a new group of people or in unfamiliar situations. Feeling inferior comes from a sense of comparison that may not be fair. Know that you have strengths that others don't. Remind yourself of your strengths.",
            R.drawable.worthless,
            "-4",
            "-1"
        )
        expressionMap["123"] = UserExpressionList(
            "123",
            "Infuriated",
            "Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. When you are calmer, don't forget to introspect if you would like your brain to react differently in such a situation. ",
            R.drawable.annoyed,
            "-4",
            "4"
        )
        expressionMap["220"] = UserExpressionList(
            "220",
            "Inquisitive",
            "If your inquiries aren't coming out of insecurities, explore them. But if they are, let the moment pass. Encouraging inquisitiveness out of insecurities isn't always healthy.",
            R.drawable.naughty,
            "1",
            "3"
        )
        expressionMap["83"] = UserExpressionList(
            "83",
            "Insecure",
            "We understand that it must be hard on you. We all experience it from time to time, especially if we’re in a new group of people or in unfamiliar situations. Talk to a friend you trust and share your fears. You may get a perspective that reduces the sense of insecurity. ",
            R.drawable.insecure,
            "-4",
            "1"
        )
        expressionMap["111"] = UserExpressionList(
            "111",
            "Insignificant",
            "A difficult emotion to go through. But, like all phases of life, this will pass too. Acknowledge the feeling, and cry if you feel like it. If possible, engage in activities that touch you at a deeper level. Talk to a friend who understands you, do something for the needy, or engage in an old hobby.",
            R.drawable.sad,
            "-4",
            "-2"
        )
        expressionMap["84"] = UserExpressionList(
            "84",
            "Inspired",
            "A great mood to be in. Talk to friends and share your energy. You may end up lifting their spirits as well. When you are in a good mood, it is easier to make others smile. Maybe, send notes of appreciation to friends you like. Expressing gratitude will help you maintain a good mood.",
            R.drawable.inspired,
            "4",
            "4"
        )
        expressionMap["63"] = UserExpressionList(
            "63",
            "Irritated",
            "Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. Later, don't forget to introspect if you would like your brain to react differently in the future. ",
            R.drawable.irritated,
            "-2",
            "2"
        )
        expressionMap["128"] = UserExpressionList(
            "128",
            "Isolated",
            "Not everyone's company is meant for us. Take your time to build your tribe. All it takes is one heartfelt conversation with someone to feel connected. Think of people who have made you feel warm in the past and with whom you have lost touch unintentionally. Try to get in touch with them. They may enjoy hearing from you as much as you enjoy speaking to them.",
            R.drawable.lonely,
            "-4",
            "-3"
        )
        expressionMap["150"] = UserExpressionList(
            "150",
            "Jealous",
            "Great that you know you are being jealous. We all experience it from time to time, especially if we’re in a new group of people or in an unfamiliar situation. Talk to a friend you trust. Try to not let this emotion influence your actions. ",
            R.drawable.guilty,
            "-3",
            "2"
        )
        expressionMap["186"] = UserExpressionList(
            "186",
            "Jittery",
            "It is an uncomfortable situation. But it is normal, and everyone goes through it during different life events. Take a few deep breaths. Focus on what you can do. A friend may help you see this situation from a less stressful perspective. Talk and share if possible.",
            R.drawable.anxious,
            "-2",
            "5"
        )
        expressionMap["228"] = UserExpressionList(
            "228",
            "Joyful",
            "We hope you feel this way forever. Journal what made you joyful. Reading this later will always lift your spirits.",
            R.drawable.cheerful,
            "2",
            "1"
        )
        expressionMap["140"] = UserExpressionList(
            "140",
            "Judgemental",
            "Kudos! Most people don't realize their shortcomings and particularly when they are being judgemental. Everything else is easy. Guide your thoughts through the lens of compassion. Judgements will slowly melt away. Oh, and one more thing, try sharing this realization that you are being judgemental with friends. That will inspire them to examine their thought process.",
            R.drawable.frustrated,
            "-3",
            "-1"
        )
        expressionMap["141"] = UserExpressionList(
            "141",
            "Let Down",
            "Not a pleasant emotion. We hope you can put your attention on other things around you. Hopes and expectations have both pros and cons. When we have them, we keep looking forward to something. But if they aren't met, we feel bad. If possible, avoid letting this incident overshadow previous positive experiences. Share how you are feeling with a trusted friend.",
            R.drawable.disappointed,
            "-3",
            "-1"
        )
        expressionMap["234"] = UserExpressionList(
            "234",
            "Lively",
            "Enjoy your time. If you have a few minutes, call a friend you have not spoken to in a while.",
            R.drawable.happy,
            "2",
            "3"
        )
        expressionMap["101"] = UserExpressionList(
            "101",
            "Livid",
            "Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. Later, don't forget to introspect if you would like your brain to react differently in the future.",
            R.drawable.angry,
            "-5",
            "4"
        )
        expressionMap["72"] = UserExpressionList(
            "72",
            "Lonely",
            "Not everyone's company is meant for us. Take your time to build your tribe. All it takes is one heartfelt conversation with someone to feel connected. Think of people who have made you feel warm in the past and with whom you have lost touch unintentionally. Try to get in touch with them. They may enjoy hearing from you as much as you enjoy speaking to them.",
            R.drawable.lonely,
            "-3",
            "-3"
        )
        expressionMap["64"] = UserExpressionList(
            "64",
            "Loving",
            "Wish you continue to be loving forever. Meet up with a close friend or family member and make them awkward by telling them how much you love them. Call a friend you have not spoken to in a while and tell them what you appreciate about them.",
            R.drawable.loving,
            "4",
            "-1"
        )
        expressionMap["155"] = UserExpressionList(
            "155",
            "Mad",
            "It is absolutely normal to feel this way sometimes. Use humor to release tension. Let this pass.",
            R.drawable.angry,
            "-3",
            "3"
        )
        expressionMap["212"] = UserExpressionList(
            "212",
            "Mellow",
            "Enjoy this feeling. If you have time, do something nice for someone. Call a friend. Positive energy helps others feel better. Take a moment to note down the reason behind this feeling. It will help you later when you aren't feeling great.",
            R.drawable.sleepy,
            "1",
            "-4"
        )
        expressionMap["109"] = UserExpressionList(
            "109",
            "Miserable",
            "A difficult emotion to go through. But, like all phases of life, this will pass too. Acknowledge the feeling, and cry if you feel like it. If possible, engage in activities that touch you at a deeper level. Talk to a friend who understands you, do something for the needy, or engage in an old hobby.",
            R.drawable.depressed,
            "-4",
            "-3"
        )
        expressionMap["112"] = UserExpressionList(
            "112",
            "Morose",
            "Take a walk. Sometimes some fresh air and a little quiet time can change your perspective. Call a close friend or family member. Sometimes venting your feelings can help you process them.",
            R.drawable.lonely,
            "-4",
            "-2"
        )
        expressionMap["254"] = UserExpressionList(
            "254",
            "Motivated",
            "A wonderful way to be. Hope your motivation rubs off on others around you. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better. Journal the reasons behind why you are feeling this way. It will be helpful later.",
            R.drawable.energetic,
            "3",
            "4"
        )
        expressionMap["65"] = UserExpressionList(
            "65",
            "Naughty",
            "Close friends and family are perfect targets. Play an innocent prank on one of them.",
            R.drawable.naughty,
            "2",
            "3"
        )
        expressionMap["182"] = UserExpressionList(
            "182",
            "Nervous",
            "It is an uncomfortable situation. But it is normal, and everyone goes through it during different life events. It may even be helpful in situations that need our 100% focus. Just focus on what you can do. ",
            R.drawable.scared,
            "-2",
            "3"
        )
        expressionMap["189"] = UserExpressionList(
            "189",
            "Numb",
            "It is alright. You may feel that way while handling multiple things at once. Talk about those feelings to someone you trust. Like all other phases of life, this will pass as well.",
            R.drawable.confused,
            "-1",
            "-4"
        )
        expressionMap["265"] = UserExpressionList(
            "265",
            "Optimistic",
            "We wish more people feel optimistic. Good luck with whatever you are working on. Thinking about positive events in your life and feeling gratitude for those who contributed will help you stay optimistic forever. ",
            R.drawable.relaxed,
            "4",
            "3"
        )
        expressionMap["158"] = UserExpressionList(
            "158",
            "Out-of-Control",
            "You may feel that things are out of control. Determine what you can control. Be kind to yourself. Sometimes a calmer mind finds creative ways to deal with an overwhelming situation. Engage in activities that you enjoy. Share how you are feeling with a friend.",
            R.drawable.anxious,
            "-3",
            "5"
        )
        expressionMap["159"] = UserExpressionList(
            "159",
            "Overwhelmed",
            "We all face overwhelming phases of life. Writing down why you feel overwhelmed or anxious is a great way to help alleviate those feelings. It helps to do this unstructured – having a written stream of consciousness allows you to express yourself freely, and getting those thoughts out of your head will be a relief. Sometimes a calmer mind finds creative ways to deal with an overwhelming situation.",
            R.drawable.tense,
            "-3",
            "5"
        )
        expressionMap["124"] = UserExpressionList(
            "124",
            "Panicked",
            "Take deep breaths. Stay where you are. Do not make rash decisions. Let the anxiety calm down, and then think about what you can do in this situation.",
            R.drawable.scared,
            "-4",
            "5"
        )
        expressionMap["240"] = UserExpressionList(
            "240",
            "Peaceful",
            "We wish more people get to feel this way. Hope you can maintain your calm for as long as possible. Talk to friends you have not spoken to in a while. Your peace may help them feel a bit more peaceful as well.",
            R.drawable.relaxed,
            "3",
            "-4"
        )
        expressionMap["200"] = UserExpressionList(
            "200",
            "Peeved",
            "Ignore people or things that are annoying you if you can. If you can't, try your best to keep your calm around them. Fun challenge: Observe your thoughts. Sometimes you will make interesting observations about your own mind. For example, you may find that your brain is getting worked up just because of its preferences. And that nobody is intentionally doing anything to annoy you.",
            R.drawable.irritated,
            "-1",
            "1"
        )
        expressionMap["201"] = UserExpressionList(
            "201",
            "Perplexed",
            "It is hard. Be patient, stick to what you know, and do it confidently.",
            R.drawable.angry,
            "-1",
            "1"
        )
        expressionMap["129"] = UserExpressionList(
            "129",
            "Persecuted",
            "Other people’s negative emotions will be out there and won’t always be planned. They will take their toll no matter what. Take the time to recover- Do nothing or make no plans. Find your “me” time to re-center and re-focus.",
            R.drawable.sad,
            "-3",
            "-3"
        )
        expressionMap["93"] = UserExpressionList(
            "93",
            "Pessimistic",
            "We feel pessimistic due to our bad experiences. Unfortunately, feeling that way for an extended period may come in the way of our growth and happiness. One way to feel less pessimistic is to remind ourselves of the good events of our life.",
            R.drawable.guilty,
            "-5",
            "-2"
        )
        expressionMap["262"] = UserExpressionList(
            "262",
            "Playful",
            "Who wouldn't want to be in your company right now? We hope you have a lot of fun doing things that you enjoy. Considering making some friends smile or shy by giving them compliments. ",
            R.drawable.naughty,
            "4",
            "1"
        )
        expressionMap["216"] = UserExpressionList(
            "216",
            "Pleasant",
            "Aren't you lucky! Everyone would love to feel like this. Take a moment to feel gratitude in your heart for everything and everyone responsible for this.",
            R.drawable.relaxed,
            "1",
            "1"
        )
        expressionMap["217"] = UserExpressionList(
            "217",
            "Pleased",
            "Glad you are feeling this way. Express gratitude in your mind or out loud to those who have made you feel this way. Journal the reasons on Onourem. Reading these reasons will help you later when you aren't feeling up to the mark.",
            R.drawable.satisfied,
            "1",
            "2"
        )
        expressionMap["237"] = UserExpressionList(
            "237",
            "Powerful",
            "Glad that you are feeling powerful. Hope this doesn't grow your ego, and you stay grounded and compassionate.",
            R.drawable.relaxed,
            "2",
            "4"
        )
        expressionMap["104"] = UserExpressionList(
            "104",
            "Powerless",
            "One of the toughest phases of life. But like all phases, this will pass too. Be patient. While this continues, engage in meaningful activities that give you satisfaction. Talk to friends who can understand your pain or read books that put life's phases in perspective.",
            R.drawable.sad,
            "-4",
            "-5"
        )
        expressionMap["160"] = UserExpressionList(
            "160",
            "Pressured",
            "Life sometimes brings us out of our comfort zone. What matters is whether we can keep our calm and do our best given all the constraints. These situations are good opportunities for us to observe our thoughts. Are we thinking more about the possible outcomes or spending more energy doing the things we can. Consciously changing our thought patterns empowers us.",
            R.drawable.anxious,
            "-3",
            "5"
        )
        expressionMap["263"] = UserExpressionList(
            "263",
            "Proud",
            "Enjoy the feeling. You are probably already working on this but just in case, make sure the pride doesn't boost your ego. Stay grounded and maintain humility. Know that many people and circumstances beyond your control have also contributed. Think of those and feel grateful.",
            R.drawable.naughty,
            "4",
            "2"
        )
        expressionMap["178"] = UserExpressionList(
            "178",
            "Provoked",
            "Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. Later, don't forget to introspect if you would like your brain to react differently in the future. ",
            R.drawable.irritated,
            "-2",
            "2"
        )
        expressionMap["130"] = UserExpressionList(
            "130",
            "Rejected",
            "Take your time to settle with the emotion. We all face rejection sometime or the other. What is most important is to not let rejection define us. Sometimes we could have done something differently, but sometimes there was really nothing we could have done. The best is to learn from it and move on. We can chose to start a new journey.",
            R.drawable.sad,
            "-3",
            "-3"
        )
        expressionMap["75"] = UserExpressionList(
            "75",
            "Relaxed",
            "Lovely! Enjoy the moment. Don't forget to note down on Onourem any specific incident that has made you feel this way. If you feel like it, check on friends.",
            R.drawable.relaxed,
            "1",
            "-3"
        )
        expressionMap["133"] = UserExpressionList(
            "133",
            "Remorseful",
            "The fact that you are feeling remorseful highlights the positive in you. It means that you care about how your actions impact others and that your ego isn't big enough to not recognize your mistake. If possible, express how you feel to those you may have hurt, intentionally or unintentionally. Introspect to identify how you can act differently in the future.",
            R.drawable.guilty,
            "-3",
            "-2"
        )
        expressionMap["98"] = UserExpressionList(
            "98",
            "Repelled",
            "That must be very uncomfortable. Not all situations or people value what we do. Sometimes we can do something, and sometimes we can't do anything to improve the situation. Use this opportunity to observe how your brain reacts to discomfort. If this feeling continues, engage in activities that can break the chain of negative emotions. Call a friend.",
            R.drawable.insecure,
            "-5",
            "1"
        )
        expressionMap["99"] = UserExpressionList(
            "99",
            "Repulsed",
            "That must be very uncomfortable. Not all situations or people value what we do. Sometimes we can do something, and sometimes we can't do anything to improve the situation. Use this opportunity to observe how your brain reacts to discomfort. If this feeling continues, engage in activities that can break the chain of negative emotions. Call a friend.",
            R.drawable.angry,
            "-5",
            "1"
        )
        expressionMap["247"] = UserExpressionList(
            "247",
            "Respected",
            "Such a great feeling! Make sure to not let your ego grow. Stay grounded, and be kind and compassionate. Use this positive mindset to build wholesome habits. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better.",
            R.drawable.satisfied,
            "3",
            "1"
        )
        expressionMap["241"] = UserExpressionList(
            "241",
            "Restful",
            "We wish more people get to feel this way. Hope you can maintain your calm for as long as possible. Talk to friends you have not spoken to in a while. Your peace may help them feel a bit more peaceful as well.",
            R.drawable.relaxed,
            "3",
            "-3"
        )
        expressionMap["85"] = UserExpressionList(
            "85",
            "Restless",
            "Remember, you are not alone. Build a realistic timeline or a doable short-term goal to get a sense of accomplishment. A friend may help you see this situation in a less stressful way.",
            R.drawable.restless,
            "-1",
            "3"
        )
        expressionMap["95"] = UserExpressionList(
            "95",
            "Revolted",
            "Acknowledge the feeling. Respond to the situation by understanding the root cause of the feeling. A friend may be able to help you look at the situation differently.",
            R.drawable.insecure,
            "-5",
            "-1"
        )
        expressionMap["120"] = UserExpressionList(
            "120",
            "Ridiculed",
            "An uncomfortable emotion to go through. You feeling ridiculed says more about those who made you feel this way. Share how you are feeling with a friend. If you have made a mistake, learn from it and move on.",
            R.drawable.lonely,
            "-4",
            "2"
        )
        expressionMap["161"] = UserExpressionList(
            "161",
            "Rushed",
            "Life sometimes brings us out of our comfort zone. What matters is whether we can keep our calm and do our best given all the constraints. These situations are good opportunities for us to observe our thoughts.",
            R.drawable.anxious,
            "-3",
            "5"
        )
        expressionMap["66"] = UserExpressionList(
            "66",
            "Sad",
            "Everyone feels this way once in a while. Take some time to understand the root cause of what makes you feel this way and see if there are better ways to look at things. Share how you are feeling with a friend.",
            R.drawable.sad,
            "-2",
            "-2"
        )
        expressionMap["76"] = UserExpressionList(
            "76",
            "Satisfied",
            "We wish more people get to feel this way. Hope you can maintain your calm for as long as possible. Talk to friends you have not spoken to in a while. Your peace may help them feel a bit more peaceful as well. Spend a few minutes feeling gratitude in your heart for all those who contribute to your life.",
            R.drawable.satisfied,
            "3",
            "-2"
        )
        expressionMap["67"] = UserExpressionList(
            "67",
            "Scared",
            "Take a few deep breaths. Apprehension about trying new things is general. Don't try to be perfect. A friend may help you see this situation from a less stressful perspective. Talk and share if possible. It is difficult to think clearly when we are afraid. Pray.",
            R.drawable.scared,
            "-2",
            "3"
        )
        expressionMap["226"] = UserExpressionList(
            "226",
            "Secure",
            "A great feeling. We wish more people feel like this. If you feel like it, spend some time feeling gratitude for those who contribute to your life. ",
            R.drawable.satisfied,
            "2",
            "-2"
        )
        expressionMap["145"] = UserExpressionList(
            "145",
            "Sensitive",
            "Everyone feels this way once in a while. Take some time to understand the root cause of what makes you feel this way and see if there are better ways to look at things. Share how you are feeling with a friend.",
            R.drawable.confused,
            "-3",
            "1"
        )
        expressionMap["268"] = UserExpressionList(
            "268",
            "Serene",
            "Enjoy the moment. We wish you get to feel this emotion often. You can enhance this experience by thinking of all the people who have contributed to your life. Feel grateful to them. If you feel like it, tell them you appreciate their contribution to your life.",
            R.drawable.relaxed,
            "5",
            "-5"
        )
        expressionMap["207"] = UserExpressionList(
            "207",
            "Shocked",
            "Breathe. Take your time to let this phase pass. Take time to understand the incident better. Respond if you need to when you are calm. What will matter in the long run is how you respond to unexpected situations. Talking to a friend may help get a different perspective.",
            R.drawable.surprised,
            "-1",
            "5"
        )
        expressionMap["68"] = UserExpressionList(
            "68",
            "Silly",
            "Wonderful. Not many people realize when they are being silly. Call a close friend.",
            R.drawable.silly,
            "2",
            "1"
        )
        expressionMap["146"] = UserExpressionList(
            "146",
            "Skeptical",
            "Great that you know you are being skeptical. This emotion helps us safeguard our interests. But when we are skeptical in general, it limits our growth and experiences. So introspect, is there a past experience in this domain that warrants you to be skeptical in this situation?",
            R.drawable.tired,
            "-3",
            "1"
        )
        expressionMap["69"] = UserExpressionList(
            "69",
            "Sleepy",
            "We hope you get to rest soon. But if this is happening even after you have had enough sleep, move around to let this pass. If this is happening very often, talk to a doctor to understand if you need to make any changes in diet or routine.",
            R.drawable.sleepy,
            "1",
            "-5"
        )
        expressionMap["134"] = UserExpressionList(
            "134",
            "Sorry",
            "The fact that you are feeling sorry highlights the positive in you. It means that you care about how your actions impact others and that your ego isn't big enough to not recognize your mistake. If possible, express how you feel to those you may have hurt, intentionally or unintentionally. Introspect to identify how you can act differently in the future.",
            R.drawable.guilty,
            "-3",
            "-2"
        )
        expressionMap["163"] = UserExpressionList(
            "163",
            "Spent",
            "Kudos to all the efforts you made. Take a break if you can. If not, know that this hard work will make you feel satisfied once this is done. Breathe. Keep your calm and do what needs to be done. Make sure to rejuvenate as soon as you can.",
            R.drawable.tired,
            "-2",
            "-5"
        )
        expressionMap["208"] = UserExpressionList(
            "208",
            "Startled",
            "Breathe. Take your time to let this phase pass. Take time to understand the incident better. Respond if you need to when you are calm. What will matter in the long run is how you respond to unexpected situations. Talking to a friend may help get a different perspective.",
            R.drawable.surprised,
            "-1",
            "5"
        )
        expressionMap["162"] = UserExpressionList(
            "162",
            "Stressed",
            "Life sometimes brings us out of our comfort zone. What matters is whether we can keep our calm and do our best given all the constraints. These situations are good opportunities for us to observe our thoughts. Are we thinking more about the possible outcomes or spending more energy doing the things we can. Consciously changing our thought patterns empowers us. A friend may help you see this situation from a less stressful perspective.",
            R.drawable.tense,
            "-3",
            "5"
        )
        expressionMap["205"] = UserExpressionList(
            "205",
            "Stunned",
            "Breathe. Take your time to let this phase pass. Take time to understand the incident better. Respond if you need to when you are calm. What will matter in the long run is how you respond to unexpected situations. Talking to a friend may help get a different perspective.",
            R.drawable.surprised,
            "-1",
            "4"
        )
        expressionMap["264"] = UserExpressionList(
            "264",
            "Successful",
            "Enjoy the feeling. You are probably already working on this but just in case, make sure the pride doesn't boost your ego. Stay grounded and maintain humility. Know that many people and circumstances beyond your control have also contributed. Think of those and feel grateful.",
            R.drawable.naughty,
            "4",
            "2"
        )
        expressionMap["126"] = UserExpressionList(
            "126",
            "Sullen",
            "Everyone feels this way once in a while. Take some time to understand the root cause of what makes you feel this way and see if there are better ways to look at things. Share how you are feeling with a friend.",
            R.drawable.sad,
            "-4",
            "-4"
        )
        expressionMap["78"] = UserExpressionList(
            "78",
            "Surprised",
            "We hope you are pleasantly surprised. If not, see if you can use this opportunity to understand why it happened. Journal, what surprised you. You will enjoy reading it later.",
            R.drawable.surprised,
            "1",
            "5"
        )
        expressionMap["80"] = UserExpressionList(
            "80",
            "Tense",
            "Take a few deep breaths. A friend may help you see this situation from a less stressful perspective. Talk and share if possible. It is difficult to think clearly when we are afraid. Pray.",
            R.drawable.tense,
            "-2",
            "4"
        )
        expressionMap["110"] = UserExpressionList(
            "110",
            "Terrible",
            "A difficult emotion to go through. But, like all phases of life, this will pass too. Acknowledge the feeling, and cry if you feel like it. If possible, engage in activities that touch you at a deeper level. Talk to a friend who understands you, do something for the needy, or engage in an old hobby.",
            R.drawable.depressed,
            "-4",
            "-3"
        )
        expressionMap["260"] = UserExpressionList(
            "260",
            "Thankful",
            "Gratitude brings so many benefits to those who feel it. Use this moment to appreciate all those who have contributed to your life. If you have the energy, give some of them a call and appreciate them.",
            R.drawable.relaxed,
            "4",
            "-2"
        )
        expressionMap["224"] = UserExpressionList(
            "224",
            "Thoughtful",
            "Being thoughtful is great. We hope you are at ease and will keep adding value to people's lives.",
            R.drawable.relaxed,
            "2",
            "-4"
        )
        expressionMap["185"] = UserExpressionList(
            "185",
            "Threatened",
            "Take a few deep breaths. A friend may help you see this situation from a less stressful perspective. Talk and share if possible.",
            R.drawable.scared,
            "-2",
            "4"
        )
        expressionMap["274"] = UserExpressionList(
            "274",
            "Thrilled",
            "One of those moments you will remember for life. Best wishes for your plans and goals. Have fun! A small challenge for you. Close your eyes and observe how your breath feels around your nostrils for 5 minutes without getting distracted.",
            R.drawable.excited,
            "5",
            "2"
        )
        expressionMap["70"] = UserExpressionList(
            "70",
            "Tired",
            "We hope you get to rest soon. Kudos to all the efforts you made. Take a break if you can. If not, know that this hard work will make you feel satisfied once this is done. If you feel tired more often than you should, think about changing your diet and sleep patterns. Evaluate your thought patterns as well. Maybe you are stressed about something. Try to identify the root cause and address that.",
            R.drawable.tired,
            "-1",
            "-3"
        )
        expressionMap["271"] = UserExpressionList(
            "271",
            "Touched",
            "A great feeling. This must fill you with emotions of gratitude towards those who made you feel this way. If possible, express your appreciation to them. Share your experience with a friend. It subtly encourages similar behavior in them.",
            R.drawable.relaxed,
            "5",
            "-2"
        )
        expressionMap["179"] = UserExpressionList(
            "179",
            "Touchy",
            "Everyone feels this way once in a while. Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. Later, don't forget to introspect if you would like your brain to react differently in such a situation. Share how you are feeling with a friend.",
            R.drawable.irritated,
            "-2",
            "2"
        )
        expressionMap["239"] = UserExpressionList(
            "239",
            "Tranquil",
            "We wish more people get to feel this way. Hope you can maintain your calm for as long as possible. Talk to friends you have not spoken to in a while. Your peace may help them feel a bit more peaceful as well.",
            R.drawable.relaxed,
            "3",
            "-5"
        )
        expressionMap["117"] = UserExpressionList(
            "117",
            "Troubled",
            "We appreciate that you acknowledged this feeling. Talk to a friend you trust. Share what is troubling you. A friend may help look at things from a different perspective.",
            R.drawable.irritated,
            "-4",
            "1"
        )
        expressionMap["71"] = UserExpressionList(
            "71",
            "Undefined",
            "We will try to add more moods in the future to capture how you are feeling. Meanwhile, answer some questions, listen to some Vocals, or send some appreciation messages on Onourem. We are sure you will feel good.",
            R.drawable.confused,
            "0",
            "0"
        )
        expressionMap["174"] = UserExpressionList(
            "174",
            "Uneasy",
            "It is normal to feel this way sometimes. Fun challenge: Try taking your mind off the subject that makes you feel this way. Engage in activities that make you feel good. You may find it difficult to take your mind off. Observe your brain thought pattern. Know that this uneasy feeling will pass with time. Breathe through it.",
            R.drawable.confused,
            "-2",
            "1"
        )
        expressionMap["192"] = UserExpressionList(
            "192",
            "Unfocused",
            "Glad you noticed. Most people don't even realize when they aren't able to focus. Take a break if you can. Plan and set specific times for specific tasks to feel calmer while working on one thing, knowing that you will have time later to address other matters.",
            R.drawable.worthless,
            "-1",
            "-3"
        )
        expressionMap["238"] = UserExpressionList(
            "238",
            "Upbeat",
            "Great! Enjoy the experience. Count your blessings and help others.",
            R.drawable.cheerful,
            "2",
            "5"
        )
        expressionMap["248"] = UserExpressionList(
            "248",
            "Valued",
            "Such a great feeling! Make sure to not let your ego grow. Stay grounded, and be kind and compassionate. Use this positive mindset to build wholesome habits. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better.",
            R.drawable.satisfied,
            "3",
            "1"
        )
        expressionMap["167"] = UserExpressionList(
            "167",
            "Victimised",
            "A painful feeling to go through. Find the strength to stay calm and look for ways to improve the situation. If possible, share how you are feeling with a friend. Seek support or professional advice if needed. Trust this will pass. Keep looking for a solution.",
            R.drawable.depressed,
            "-2",
            "-3"
        )
        expressionMap["156"] = UserExpressionList(
            "156",
            "Violated",
            "Not everyone values what we do. Focus on whether someone really intended to hurt you, or it was just situational. When possible, learn, forgive and move on.",
            R.drawable.ashamed,
            "-3",
            "3"
        )
        expressionMap["170"] = UserExpressionList(
            "170",
            "Vulnerable",
            "Everyone feels like this one time or the other. If you are feeling too weak, just breathe. Like all phases of life, this will pass as well. When you have the energy, observe your thoughts to identify what you need to feel less fragile. Talk to a friend.",
            R.drawable.confused,
            "-2",
            "-2"
        )
        expressionMap["113"] = UserExpressionList(
            "113",
            "Weak",
            "We appreciate that you have the courage to accept how you are genuinely feeling. So many people deny such feelings and force them underground, where they can do more damage with time. Cry if you feel like it. Notice if you feel relief after the tears stop. Write down how and why you are feeling like this. Talk to a friend who understands you. ",
            R.drawable.sad,
            "-4",
            "-2"
        )
        expressionMap["175"] = UserExpressionList(
            "175",
            "Weird",
            "It is normal to feel this way sometimes. Observe your thoughts to understand the root cause of this feeling. Use this opportunity to understand yourself better. See if you can do anything to improve the situation. Introspect how you would like to handle such situations in the future. All such uncomfortable experiences are a great way to train ourselves to deal with them.",
            R.drawable.confused,
            "-2",
            "1"
        )
        expressionMap["198"] = UserExpressionList(
            "198",
            "Withdrawn",
            "A lot of people feel that way once in a while. If the feeling persists, try to understand the root cause. Is it because you are stressed? Or hurt? Or disappointed? Identify the reason behind the feeling and work on it.",
            R.drawable.tense,
            "-1",
            "-1"
        )
        expressionMap["151"] = UserExpressionList(
            "151",
            "Worried",
            "An emotion that keeps us vigilant to take the required action in an evolving situation. If you can't do anything, Pray! Provide your emotional support to those who may need it. Talk to someone you feel comfortable with.",
            R.drawable.tense,
            "-3",
            "2"
        )
        expressionMap["82"] = UserExpressionList(
            "82",
            "Worthless",
            "Sometimes, when you feel worthless, focusing your attention on something other than yourself can help. Helping others can also help you feel a greater sense of connection and purpose.",
            R.drawable.worthless,
            "-4",
            "-2"
        )

        return expressionMap
    }

    @JvmStatic
    fun getAllMoods(preferenceHelper: SharedPreferenceHelper): ArrayList<UserExpressionList> {
        val expressionList: ArrayList<UserExpressionList> = ArrayList()

        expressionList.add(
            UserExpressionList(
                "106",
                "Abandoned",
                "A sense of alienation and estrangement makes it very difficult to enjoy anything. Just know that you are unique, and not everyone can understand your feelings. Look around for things or people that make you comfortable. Keep Calm. Believe that nothing is permanent, and this feeling will disappear with time as you build new strong relationships.",
                R.drawable.lonely,
                "-4",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "243",
                "Accepted",
                "Be grateful that you are feeling this way. Enjoy the feeling and engage in wholesome activities that further enhance your experience. Talk to a friend you have not spoken to in a while. Do something kind for someone around you. Make someone feel warm.",
                R.drawable.relaxed,
                "3",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "180",
                "Afraid",
                "Take a few deep breaths. A friend may help you see this situation from a less stressful perspective. Talk and share if possible. It is difficult to think clearly when we are afraid. Pray.",
                R.drawable.scared,
                "-2",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "203",
                "Aggressive",
                "Sometimes, it may seem like a good emotion, but it makes us less considerate. So take your time and let this aggression cool off before making an important decision. Go for a run or engage in any physically tiring activity to let the steam out.",
                R.drawable.angry,
                "-1",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "92",
                "Alienated",
                "A sense of alienation and estrangement makes it very difficult to enjoy anything. Just know that you are unique, and not everyone can understand your feelings. Look around for things or people that make you comfortable. Keep Calm. Believe that nothing is permanent, and this feeling of alienation will disappear with time.",
                R.drawable.depressed,
                "-5",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "278",
                "Amazed",
                "One of those moments you will remember for life. Have fun! A small challenge for you. Close your eyes and observe how your breath feels around your nostrils for 5 minutes without getting distracted.",
                R.drawable.inspired,
                "5",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "54",
                "Angry",
                "It is absolutely normal to feel angry towards someone or something. Use humor to release tension. Avoid taking any decisions as anger clouds our judgement. Let this pass. Once you're calm, express your concerns.",
                R.drawable.angry,
                "-3",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "55",
                "Annoyed",
                "Ignore people or things that are annoying you if you can. If you can't, try your best to keep your calm around them. Fun challenge: Observe your thoughts. Sometimes you will make interesting observations about your own mind. For example, you may find that your brain is getting worked up just because of its preferences. And that nobody is intentionally doing anything to annoy you.",
                R.drawable.annoyed,
                "-1",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "86",
                "Anxious",
                "Take a few deep breaths. It is ok to feel anxious sometimes. A friend may help you see this situation from a less stressful perspective. Talk. Share. Just know that in many situations we get anxious about, their outcomes don't end up defining who we are in the long run. So focus on just giving your best. ",
                R.drawable.anxious,
                "-5",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "194",
                "Apathetic",
                "A lot of people feel that way once in a while. If the feeling persists, try to understand the root cause. Is it because you are stressed? Or hurt? Or disappointed? Identify the reason behind the emotion and work on it.",
                R.drawable.guilty,
                "-1",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "96",
                "Appalled",
                "We hope you don't have to go through this emotion again. Sometimes we can do something, and sometimes we can't do anything to improve the situation. When you can't, pray! Call a friend and share how you are feeling. Engage in soulful activities like helping others and tending to plants.",
                R.drawable.irritated,
                "-5",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "118",
                "Apprehensive",
                "Sometimes life presents situations that make us uncomfortable. Focus on what you can do to be best prepared for the upcoming situation. If you have a friend you trust, share your apprehensions. Sometimes we make things much bigger in our minds than they really are.",
                R.drawable.scared,
                "-4",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "230",
                "Aroused",
                "Acknowledge them and set them aside for later. Put on some music, and exercise regularly. Make sure to stay safe in anything you do.",
                R.drawable.excited,
                "2",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "87",
                "Ashamed",
                "We understand how it feels. But, most of the time, it is not as bad as it seems in our minds. Introspect to identify how you can act differently in the future.",
                R.drawable.ashamed,
                "-3",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "279",
                "Astonished",
                "Hope you get to spend enough time thinking about what made you feel this way.  Compliment those who made you experience this.",
                R.drawable.inspired,
                "5",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "215",
                "At ease",
                "Lovely! Enjoy the moment. If you feel like it, check on friends. Don't forget to note down on Onourem any specific incident that has made you feel this way. ",
                R.drawable.relaxed,
                "1",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "280",
                "Awe",
                "Hope you get to spend enough time thinking about what made you feel this way.  Compliment those who made you experience this.",
                R.drawable.inspired,
                "5",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "107",
                "Awful",
                "Set aside some quiet time for yourself. Mentally open the door to guilt, frustration, regret, anger, and any other emotions that might come up. Writing down what you feel can help.",
                R.drawable.depressed,
                "-4",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "171",
                "Bad",
                "Some days are like that. Breathe through this emotion. Your mind will stop focussing on this subject in some time, and it won't feel this bad. Or you can try to intentionally focus your mind on other activities that make you feel better. Talk to a friend and share how you are feeling. If you are up for a little challenge, try observing your thoughts when you are in a bad mood. This will tell you a lot about your own mind. How it reacts to situations.",
                R.drawable.lonely,
                "-2",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "270",
                "Balanced",
                "A wonderful way to be. Enjoy whatever you are doing. If you feel like it, talk to a friend you have not spoken to in a while. Find out if they are doing well and, if not, what you can do to make them feel better.",
                R.drawable.relaxed,
                "5",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "165",
                "Betrayed",
                "Sorry, you have to go through this emotion. Allow yourself the time to get over it. Observe your thoughts to ensure your brain isn't encouraging negative feelings or actions. When possible, learn, forgive and move on. If you wish to challenge yourself, try doing something nice for people around you to pull yourself out of negativity.",
                R.drawable.sad,
                "-2",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "153",
                "Bitter",
                "Everyone feels this way once in a while. Take some time to understand the root cause of what makes you feel this way and see if there are better ways to look at things. Share how you are feeling with a friend.",
                R.drawable.angry,
                "-3",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "258",
                "Blessed",
                "One of the best feelings. Use this moment to feel gratitude towards all those who have contributed to your life. If you have the energy, give some of them a call and appreciate them.",
                R.drawable.satisfied,
                "4",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "273",
                "Blissful",
                "Many people have probably not experienced this emotion in a long time. You are fortunate. Be grateful for all those who contributed to this journey. If you feel like it, check on your friends to ensure they are doing fine. Your positive energy may help them feel better. ",
                R.drawable.happy,
                "5",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "74",
                "Bored",
                "A very common feeling. Pick a new hobby or interact with your loved ones. Go out or watch a movie, dance or sleep or do whatever you want. Just don't trouble your mother. A fun challenge: Close your eyes for 5 minutes. Try to observe the sensations air creates in your nostrils as you breathe normally. You will realize how difficult it is to focus your brain on something. Your brain will keep losing track of the sensations and start thinking about something else. Read about meditation and practice it when possible if you find this interesting.",
                R.drawable.bored,
                "-1",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "195",
                "Busy",
                "Well, we hope you are enjoying being busy. If not, you get a break soon to unwind. Talk to a friend before going to bed.",
                R.drawable.anxious,
                "-1",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "214",
                "Calm",
                "We hope you can maintain this calm for as long as possible. If you feel like it, talk to a friend you haven't spoken to in a long time. Note down what made you feel calm if you have not already. This will come in handy in the future when you aren't feeling well.",
                R.drawable.relaxed,
                "1",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "269",
                "Carefree",
                "Enjoy the moment. We wish you get to feel this emotion often while being responsible. You can enhance this experience by thinking of all the people who have contributed to your life. Feel grateful to them and if possible, give them a call to express your appreciation.",
                R.drawable.relaxed,
                "5",
                "-4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "233",
                "Cheeky",
                "Close friends and family are perfect targets. Play an innocent prank on one of them. Maybe send them an appreciation message anonymously. Tell them a little later it was from you.",
                R.drawable.happy,
                "2",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "56",
                "Cheerful",
                "Great. See if you can make a friend smile today. Journal the reasons behind this feeling. Reading these later will lift your spirits.",
                R.drawable.cheerful,
                "2",
                "4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "225",
                "Chill",
                "Great. Have fun! If it is convenient, talk to a friend you have not spoken to in some time.",
                R.drawable.naughty,
                "2",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "257",
                "Comfy",
                "Glad you are feeling comfortable. Enjoy. Close your eyes and think of all the people who have contributed to your life. Express gratitude to them in your heart. If you feel like it, give some of them a call and say Thank You to them.",
                R.drawable.satisfied,
                "4",
                "-4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "223",
                "Complacent",
                "It is great to feel satisfied in life. Not many people get to feel this emotion. While you are at peace, keep thinking about how to improve yourself.",
                R.drawable.satisfied,
                "2",
                "-5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "142",
                "Concerned",
                "An emotion that keeps us vigilant to take the required action in an evolving situation. If you can't do anything, Pray! Provide your emotional support to those who may need it.",
                R.drawable.tense,
                "-3",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "249",
                "Confident",
                "Such a great feeling! Make sure to not let your ego grow. Stay grounded, be kind and compassionate. Use this positive mindset to build wholesome habits. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better.",
                R.drawable.energetic,
                "3",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "57",
                "Confused",
                "Lucky you! You have got options. A lot of people don't. If you are tired of thinking and this isn't urgent, maybe sleep on it. And take a decision. Sometimes, talking to a friend helps.",
                R.drawable.confused,
                "-1",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "244",
                "Content",
                "Be grateful that you are feeling this way. Enjoy the feeling and engage in wholesome activities that further enhance your experience. Talk to a friend you have not spoken to in a while. Do something kind for someone around you. Make someone feel warm.",
                R.drawable.relaxed,
                "3",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "253",
                "Courageous",
                "That must feel good. Wish you all the best in your goals. Make sure to stay grounded, kind and compassionate.",
                R.drawable.energetic,
                "3",
                "4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "256",
                "Cozy",
                "Enjoy this time. If you feel like it, talk to a friend you have not spoken to in a while. Otherwise, a fun challenge: Close your eyes for 5 minutes. Try to observe the sensations air creates in your nostrils as you breathe normally. You will realize how difficult it is to focus your brain on something. Your brain will keep losing track of the sensations and start thinking about something else. Read about meditation and practice it when possible if you find this interesting.",
                R.drawable.relaxed,
                "4",
                "-5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "251",
                "Creative",
                "A good mood to be in. Show us your creativity in your interactions on Onourem. :-)",
                R.drawable.naughty,
                "3",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "143",
                "Critical",
                "An emotion that serves well in some situations and not so well in others. If you really need to be critical right now, try being compassionate and kind at the same time.",
                R.drawable.embarrassed,
                "-3",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "218",
                "Curious",
                ":-) Hopefully, your curiosity is positive and constructive. If you are curious to find out facts others may have not shared with you yet, be patient. ",
                R.drawable.naughty,
                "1",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "73",
                "Depressed",
                "We care for you, and so do your friends and family, even if they have not expressed it lately. When you feel comfortable, share how you feel with someone you trust or reach out to a support group. If this phase continues, talking to a therapist may be the best thing to do. Meanwhile, invest your energy in soulful activities like helping the needy or teaching someone something. Try new things.",
                R.drawable.depressed,
                "-4",
                "-4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "125",
                "Desolate",
                "A difficult emotion to go through. But, like all phases of life, this will pass too. Acknowledge the feeling, and cry if you feel like it. If possible, engage in activities that touch you at a deeper level. Talk to a friend who understands you, do something for the needy, or engage in an old hobby.",
                R.drawable.sad,
                "-4",
                "-5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "89",
                "Despair",
                "Feeling defeated or losing hope? Keep breathing. Acknowledge your emotion instead of hiding or avoiding it. It may seem impossible right now, but even this stage of life will pass.",
                R.drawable.depressed,
                "-5",
                "-5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "90",
                "Despondent",
                "Expecting no improvement? This might very well be the most challenging emotion to deal with. When you feel comfortable, share how you feel with someone you trust. ",
                R.drawable.depressed,
                "-5",
                "-4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "79",
                "Disappointed",
                "Not a pleasant emotion. We hope you can put your attention on other things around you. Hopes and expectations have both pros and cons. When we have them, we keep looking forward to something. But if they aren't met, we feel bad. If possible, avoid letting this incident overshadow previous positive experiences.",
                R.drawable.disappointed,
                "-3",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "136",
                "Disapproving",
                "Glad you have noticed that you are feeling this way. Try that your emotion doesn't influence your decisions and makes them unfair to others around you.",
                R.drawable.tense,
                "-3",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "131",
                "Discouraged",
                "It's definitely not easy to deal with such a feeling. But remember that something or someone that kept you going till now. Feel proud of how far you've come. The upside of discouragement is it may motivate you to evaluate yourself. Reinterpret your setbacks as learning experiences and stepping stones.",
                R.drawable.disappointed,
                "-3",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "94",
                "Disgusted",
                "Breathe. Pay attention to what really triggered the emotion and your reaction. Think of how you would like to manage situations that may trigger this emotion again.",
                R.drawable.insecure,
                "-5",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "166",
                "Disheartened",
                "Sorry, you have to go through this emotion. Allow yourself the time to get over it. Observe your thoughts to ensure your brain isn't encouraging negative feelings or actions. When possible, learn, forgive and move on. If you wish to challenge yourself, try doing something nice for people around you to pull yourself out of negativity.",
                R.drawable.sad,
                "-2",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "137",
                "Disillusioned",
                "Hopefully, it is for the best that our expectations have been reset. Resist bitterness. Introspect the sequence of events that eventually led to this emotion. Identify any adjustments we need to make in our thinking process.",
                R.drawable.lonely,
                "-3",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "206",
                "Dismayed",
                "Breathe. Take your time to let this phase pass. Take time to understand the incident better. Respond if you need to when you are calm. What will matter in the long run is how you respond to unexpected situations. Talking to a friend may help get a different perspective.",
                R.drawable.restless,
                "-1",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "144",
                "Dismissive",
                "Kudos! Most people don't realize when they are in a subtly bad mood. Everything else is easy. Guide your thoughts through the lens of compassion. Try to be fair and not let your mood influence your decisions.",
                R.drawable.worthless,
                "-3",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "138",
                "Disrespected",
                "Not a pleasant emotion. Introspect to identify if any of your actions may have invoked a disrespectful reaction from others. It is not a justification for disrespecting anyone, but it is best to first focus on our actions. Hopefully, the incident doesn't damage your own thought process by invoking the desire to hurt others. Be the bigger human being. Introspect, forgive and move on.",
                R.drawable.lonely,
                "-3",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "196",
                "Distant",
                "A lot of people feel that way once in a while. If the feeling persists, try to understand the root cause. Is it because you are stressed? Or hurt? Or disappointed? Identify the reason behind the feeling and work on it. Talk to a close friend.",
                R.drawable.embarrassed,
                "-1",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "209",
                "Don't Know",
                "That is alright! Answer some questions, listen to some Vocals, or send some appreciation messages on Onourem. We are sure you will feel good.",
                R.drawable.confused,
                "0",
                "0"
            )
        )
        expressionList.add(
            UserExpressionList(
                "172",
                "Down",
                "Some days are like that. Breathe through this emotion. Your mind will stop focussing on this subject in some time, and it won't feel this bad. Or you can try to intentionally focus your mind on other activities that make you feel better. Talk to a friend and share how you are feeling. If you are up for a little challenge, try observing your thoughts when you are in a bad mood. This will tell you a lot about your own mind. How it reacts to situations.",
                R.drawable.lonely,
                "-2",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "187",
                "Drained",
                "Kudos to all the efforts you made. Take a break if you can. If not, know that this hard work will make you feel satisfied once this is done. Breathe. Keep your calm and do what needs to be done. Make sure to rejuvenate as soon as you can.",
                R.drawable.tired,
                "-1",
                "-5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "275",
                "Eager",
                "Happy that you are looking forward to something. Sometimes we lose control of our actions in excitement. Hopefully, you can avoid that while having fun.",
                R.drawable.cheerful,
                "5",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "227",
                "Easygoing",
                "A wonderful way to be. Enjoy whatever you are doing. If you feel like it, talk to a friend you have not spoken to in a while. Find out if they are doing well and, if not, what you can do to make them feel better.",
                R.drawable.cheerful,
                "2",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "281",
                "Ecstatic",
                "Hope you get to spend enough time thinking about what made you feel this way.  Compliment those who made you experience this.",
                R.drawable.cheerful,
                "5",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "277",
                "Elated",
                "One of those moments you will remember for life. Have fun! A small challenge for you. Close your eyes and observe how your breath feels around your nostrils for 5 minutes without getting distracted.",
                R.drawable.energetic,
                "5",
                "4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "81",
                "Embarrassed",
                "We understand how it feels. But, most of the time, it is not as bad as it seems in our minds. Introspect to identify how you can act differently in the future.",
                R.drawable.embarrassed,
                "-3",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "105",
                "Empty",
                "Gently acknowledge the emptiness. If you recognize that your feelings are linked to a loss you experienced, consider allowing yourself time and space to grieve openly. Grief looks and feels different to everyone, and there are no right or wrong ways to do it.",
                R.drawable.lonely,
                "-4",
                "-4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "88",
                "Energetic",
                "Great. See if you can bring a smile to a friend's face today. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better.",
                R.drawable.energetic,
                "1",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "102",
                "Enraged",
                "Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. When you are calmer, don't forget to introspect if you would like your brain to react differently in the future.",
                R.drawable.irritated,
                "-5",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "252",
                "Enthusiastic",
                "A wonderful way to be. Hope your enthusiasm rubs off on others around you. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better. Journal the reasons behind why you are feeling this way. It will help you later reading it.",
                R.drawable.excited,
                "3",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "58",
                "Excited",
                "Best wishes for your plans and goals. Have fun! A small challenge for you. Close your eyes and observe how your breath feels around your nostrils for 5 minutes without getting distracted.",
                R.drawable.excited,
                "5",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "127",
                "Excluded",
                "Take your time to understand the reasons behind your feelings, learn from them and move on. Look around for things or people that make you comfortable. ",
                R.drawable.lonely,
                "-4",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "164",
                "Exhausted",
                "Kudos to all the efforts you made. Take a break if you can. If not, know that this hard work will make you feel satisfied once this is done. Breathe. Keep your calm and do what needs to be done. Make sure to rejuvenate as soon as you can.",
                R.drawable.tired,
                "-2",
                "-4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "267",
                "Exhilarated",
                "You must be a source of positive energy right now. Have fun. Dance if you like. Call a close friend and share your energy. Journal your thoughts. You will find them very handy to read later when you feel low.",
                R.drawable.cheerful,
                "4",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "149",
                "Exposed",
                "We understand how it feels. But, most of the time, it is not as bad as it seems in our minds. Introspect to identify how you can act differently in the future.",
                R.drawable.embarrassed,
                "-3",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "188",
                "Fatigued",
                "Kudos to all the efforts you made. Take a break if you can. If not, know that this hard work will make you feel satisfied once this is done. Breathe. Keep your calm and do what needs to be done. Make sure to rejuvenate as soon as you can.",
                R.drawable.tired,
                "-1",
                "-4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "181",
                "Fearful",
                "Take a few deep breaths. A friend may help you see this situation from a less stressful perspective. Talk and share if possible. It is difficult to think clearly when we are afraid. Pray.",
                R.drawable.scared,
                "-2",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "255",
                "Festive",
                "Enjoy your time. If you have a few minutes, call a friend you have not spoken to in a while.",
                R.drawable.cheerful,
                "3",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "59",
                "Flirty",
                "Hmm. Go, flirt with your partner, ONLY. If you are single, stay decent and remember no means no.",
                R.drawable.flirting,
                "2",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "250",
                "Focused",
                "Continue what you have been doing. Stay grounded, and be kind and compassionate. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better.",
                R.drawable.relaxed,
                "3",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "168",
                "Fragile",
                "Everyone feels like this one time or the other. If you are feeling too weak, just breathe. Like all phases of life, this will pass as well. When you have the energy, observe your thoughts to identify what you need to feel less fragile. Talk to a friend.",
                R.drawable.confused,
                "-2",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "245",
                "Free",
                "Be grateful that you are feeling this way. Enjoy the feeling and engage in wholesome activities that further enhance your experience. Talk to a friend you have not spoken to in a while. Do something kind for someone around you. Make someone feel warm.",
                R.drawable.satisfied,
                "3",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "121",
                "Frightened",
                "Share how you are feeling with your friends. You may feel less frightened knowing you can rely on them in difficult situations.",
                R.drawable.scared,
                "-4",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "60",
                "Frustrated",
                "Everyone faces this emotion. Observe your thoughts. Sometimes you may find that the reasons behind your frustrations are real. In those cases, think of what you can do constructively. But sometimes, no one is doing anything intentionally to trouble you. It is just that your brain has got too comfortable with its own preferences and is getting worked up when these preferences are not met. In these cases, work on your own thought patterns. Remember, every individual is different. Be patient, compassionate, and kind.",
                R.drawable.frustrated,
                "-3",
                "4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "272",
                "Fulfilled",
                "Enjoy the moment. We wish you get to feel this emotion often. You can enhance this experience by thinking of all the people who have contributed to your life. Feel grateful to them. If you feel like it, tell them you appreciate them.",
                R.drawable.relaxed,
                "5",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "100",
                "Fuming",
                "Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. Later, don't forget to introspect if you would like your brain to react differently in the future. ",
                R.drawable.annoyed,
                "-5",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "122",
                "Furious",
                "Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. When you are calmer, don't forget to introspect if you would like your brain to react differently in such a situation. ",
                R.drawable.angry,
                "-4",
                "4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "114",
                "Glum",
                "A difficult emotion to go through. Talking to people with whom you feel a deeper connection may help. Go to bed and wake up at the same time every day. Keeping some routine will make you feel more structured and in control. Helping others can also help you feel a greater sense of connection and purpose. ",
                R.drawable.sad,
                "-4",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "259",
                "Grateful",
                "Gratitude brings so many benefits to those who feel it. Use this moment to appreciate all those who have contributed to your life. If you feel like it, give some of them a call and say Thank You.",
                R.drawable.relaxed,
                "4",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "91",
                "Grief",
                "It hurts so bad to lose someone or something that meant a lot to us. We understand. No words of wisdom can ever make us feel better in grief. But remember, it is important to allow yourself to express these feelings. Share with those who may have experienced a similar loss. Pray. Breathe.",
                R.drawable.depressed,
                "-5",
                "-4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "61",
                "Guilty",
                "The fact that you are feeling guilty speaks volumes about the positivity in you. Set aside some quiet time for yourself. Mentally open the door to guilt, sit with those feelings and explore them with curiosity instead of judgment. If you wish to do something to improve the situation, do it. Else learn from the incident and move on.",
                R.drawable.guilty,
                "-3",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "62",
                "Happy",
                "Great. See if you can bring a smile to a friend's face today. Express gratitude in your heart to those who made you happy. Journal what made you feel this way. You will be amazed to see how it impacts you positively later when your spirits are down.",
                R.drawable.happy,
                "2",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "108",
                "Helpless",
                "Sometimes you can do something, and sometimes you can't. When you can't, Pray! Focus on what you can control. Engage in meaningful activities to stay hopeful and provide emotional support to people around you.",
                R.drawable.depressed,
                "-4",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "173",
                "Hesitant",
                "You are hesitant. That also means you are careful, which is good. Sometimes hesitations are valid, and one should think clearly before taking a step. But sometimes, we just overthink how others may take it. Talk to a friend to take a second opinion if you aren't able to make up your mind.",
                R.drawable.confused,
                "-2",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "246",
                "Hopeful",
                "Seeing you hopeful makes us happy. Use this positive mindset to build healthy habits and a good routine if possible. Plan for what you wish to achieve in the future and start working towards it peacefully. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better.",
                R.drawable.naughty,
                "3",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "103",
                "Hopeless",
                "One of the toughest phases of life. But like all phases, this will pass too. Be patient. While this continues, engage in meaningful activities that give you satisfaction. Talk to friends who can understand your pain or read books that put life's phases in perspective.",
                R.drawable.depressed,
                "-4",
                "-5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "97",
                "Horrified",
                "One of those moments when life presents an unpleasant and shocking situation. Know that this emotion may hamper your ability to think clearly, so avoid reacting to this situation at this moment if possible. Sleep on it. When you are calmer, think of how you can contribute constructively. ",
                R.drawable.irritated,
                "-5",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "176",
                "Hostile",
                "Glad you recognize when your mood isn't right. When you know you are hostile, you can avoid making decisions that may impact others negatively. When you are calm, express your concerns. If possible, introspect so that you can handle similar situations with calm in the future.",
                R.drawable.angry,
                "-2",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "119",
                "Humiliated",
                "No matter how much we all want to avoid this emotion, most people go through this a few times in their lives. If you made a mistake, be the confident one, own it, learn from it, and move on. Share how you are feeling with a friend. ",
                R.drawable.lonely,
                "-4",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "139",
                "Hurt",
                "Not a pleasant emotion. Introspect to identify if any of your actions may have invoked a hurtful reaction from others. It is not a justification for hurting anyone, but it is best to first focus on our actions. Hopefully, the incident doesn't damage your own thought process by invoking the desire to hurt others. Be the bigger human being. Introspect, forgive and move on.",
                R.drawable.sad,
                "-3",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "221",
                "Hyper",
                "So good to have some extra energy. A lot of people will envy you for this. Hopefully, you can utilize it in constructive activities. For example, an exhaustive workout session will make you feel great.",
                R.drawable.surprised,
                "1",
                "4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "115",
                "Inadequate",
                "We’re really sorry that you feel this way! We know what it feels like to always want to do more and be better. BE NICE TO YOURSELF! You deserve to feel loved, especially by you! You’re an amazing person who has done incredible things in your life. Be kind to yourself.",
                R.drawable.worthless,
                "-4",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "197",
                "Indifferent",
                "A lot of people feel that way once in a while. If the feeling persists, try to understand the root cause. Is it because you are stressed? Or hurt? Or disappointed? Identify the reason behind the feeling and work on it. Talk to a close friend.",
                R.drawable.embarrassed,
                "-1",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "154",
                "Indignant",
                "Unfair and unjust actions of others can make us angry sometimes. Take your time to observe why you are feeling this way. See if you can do something constructive to improve the situation. Don't let the emotion take you any decisions that you may regret later.",
                R.drawable.angry,
                "-3",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "116",
                "Inferior",
                "We understand that it must be hard on you. We all experience it from time to time, especially if we're in a new group of people or in unfamiliar situations. Feeling inferior comes from a sense of comparison that may not be fair. Know that you have strengths that others don't. Remind yourself of your strengths.",
                R.drawable.worthless,
                "-4",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "123",
                "Infuriated",
                "Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. When you are calmer, don't forget to introspect if you would like your brain to react differently in such a situation. ",
                R.drawable.annoyed,
                "-4",
                "4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "220",
                "Inquisitive",
                "If your inquiries aren't coming out of insecurities, explore them. But if they are, let the moment pass. Encouraging inquisitiveness out of insecurities isn't always healthy.",
                R.drawable.naughty,
                "1",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "83",
                "Insecure",
                "We understand that it must be hard on you. We all experience it from time to time, especially if we’re in a new group of people or in unfamiliar situations. Talk to a friend you trust and share your fears. You may get a perspective that reduces the sense of insecurity. ",
                R.drawable.insecure,
                "-4",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "111",
                "Insignificant",
                "A difficult emotion to go through. But, like all phases of life, this will pass too. Acknowledge the feeling, and cry if you feel like it. If possible, engage in activities that touch you at a deeper level. Talk to a friend who understands you, do something for the needy, or engage in an old hobby.",
                R.drawable.sad,
                "-4",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "84",
                "Inspired",
                "A great mood to be in. Talk to friends and share your energy. You may end up lifting their spirits as well. When you are in a good mood, it is easier to make others smile. Maybe, send notes of appreciation to friends you like. Expressing gratitude will help you maintain a good mood.",
                R.drawable.inspired,
                "4",
                "4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "63",
                "Irritated",
                "Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. Later, don't forget to introspect if you would like your brain to react differently in the future. ",
                R.drawable.irritated,
                "-2",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "128",
                "Isolated",
                "Not everyone's company is meant for us. Take your time to build your tribe. All it takes is one heartfelt conversation with someone to feel connected. Think of people who have made you feel warm in the past and with whom you have lost touch unintentionally. Try to get in touch with them. They may enjoy hearing from you as much as you enjoy speaking to them.",
                R.drawable.lonely,
                "-4",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "150",
                "Jealous",
                "Great that you know you are being jealous. We all experience it from time to time, especially if we’re in a new group of people or in an unfamiliar situation. Talk to a friend you trust. Try to not let this emotion influence your actions. ",
                R.drawable.guilty,
                "-3",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "186",
                "Jittery",
                "It is an uncomfortable situation. But it is normal, and everyone goes through it during different life events. Take a few deep breaths. Focus on what you can do. A friend may help you see this situation from a less stressful perspective. Talk and share if possible.",
                R.drawable.anxious,
                "-2",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "228",
                "Joyful",
                "We hope you feel this way forever. Journal what made you joyful. Reading this later will always lift your spirits.",
                R.drawable.cheerful,
                "2",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "140",
                "Judgemental",
                "Kudos! Most people don't realize their shortcomings and particularly when they are being judgemental. Everything else is easy. Guide your thoughts through the lens of compassion. Judgements will slowly melt away. Oh, and one more thing, try sharing this realization that you are being judgemental with friends. That will inspire them to examine their thought process.",
                R.drawable.frustrated,
                "-3",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "141",
                "Let Down",
                "Not a pleasant emotion. We hope you can put your attention on other things around you. Hopes and expectations have both pros and cons. When we have them, we keep looking forward to something. But if they aren't met, we feel bad. If possible, avoid letting this incident overshadow previous positive experiences. Share how you are feeling with a trusted friend.",
                R.drawable.disappointed,
                "-3",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "234",
                "Lively",
                "Enjoy your time. If you have a few minutes, call a friend you have not spoken to in a while.",
                R.drawable.happy,
                "2",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "101",
                "Livid",
                "Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. Later, don't forget to introspect if you would like your brain to react differently in the future.",
                R.drawable.angry,
                "-5",
                "4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "72",
                "Lonely",
                "Not everyone's company is meant for us. Take your time to build your tribe. All it takes is one heartfelt conversation with someone to feel connected. Think of people who have made you feel warm in the past and with whom you have lost touch unintentionally. Try to get in touch with them. They may enjoy hearing from you as much as you enjoy speaking to them.",
                R.drawable.lonely,
                "-3",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "64",
                "Loving",
                "Wish you continue to be loving forever. Meet up with a close friend or family member and make them awkward by telling them how much you love them. Call a friend you have not spoken to in a while and tell them what you appreciate about them.",
                R.drawable.loving,
                "4",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "155",
                "Mad",
                "It is absolutely normal to feel this way sometimes. Use humor to release tension. Let this pass.",
                R.drawable.angry,
                "-3",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "212",
                "Mellow",
                "Enjoy this feeling. If you have time, do something nice for someone. Call a friend. Positive energy helps others feel better. Take a moment to note down the reason behind this feeling. It will help you later when you aren't feeling great.",
                R.drawable.sleepy,
                "1",
                "-4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "109",
                "Miserable",
                "A difficult emotion to go through. But, like all phases of life, this will pass too. Acknowledge the feeling, and cry if you feel like it. If possible, engage in activities that touch you at a deeper level. Talk to a friend who understands you, do something for the needy, or engage in an old hobby.",
                R.drawable.depressed,
                "-4",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "112",
                "Morose",
                "Take a walk. Sometimes some fresh air and a little quiet time can change your perspective. Call a close friend or family member. Sometimes venting your feelings can help you process them.",
                R.drawable.lonely,
                "-4",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "254",
                "Motivated",
                "A wonderful way to be. Hope your motivation rubs off on others around you. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better. Journal the reasons behind why you are feeling this way. It will be helpful later.",
                R.drawable.energetic,
                "3",
                "4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "65",
                "Naughty",
                "Close friends and family are perfect targets. Play an innocent prank on one of them.",
                R.drawable.naughty,
                "2",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "182",
                "Nervous",
                "It is an uncomfortable situation. But it is normal, and everyone goes through it during different life events. It may even be helpful in situations that need our 100% focus. Just focus on what you can do. ",
                R.drawable.scared,
                "-2",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "189",
                "Numb",
                "It is alright. You may feel that way while handling multiple things at once. Talk about those feelings to someone you trust. Like all other phases of life, this will pass as well.",
                R.drawable.confused,
                "-1",
                "-4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "265",
                "Optimistic",
                "We wish more people feel optimistic. Good luck with whatever you are working on. Thinking about positive events in your life and feeling gratitude for those who contributed will help you stay optimistic forever. ",
                R.drawable.relaxed,
                "4",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "158",
                "Out-of-Control",
                "You may feel that things are out of control. Determine what you can control. Be kind to yourself. Sometimes a calmer mind finds creative ways to deal with an overwhelming situation. Engage in activities that you enjoy. Share how you are feeling with a friend.",
                R.drawable.anxious,
                "-3",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "159",
                "Overwhelmed",
                "We all face overwhelming phases of life. Writing down why you feel overwhelmed or anxious is a great way to help alleviate those feelings. It helps to do this unstructured – having a written stream of consciousness allows you to express yourself freely, and getting those thoughts out of your head will be a relief. Sometimes a calmer mind finds creative ways to deal with an overwhelming situation.",
                R.drawable.tense,
                "-3",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "124",
                "Panicked",
                "Take deep breaths. Stay where you are. Do not make rash decisions. Let the anxiety calm down, and then think about what you can do in this situation.",
                R.drawable.scared,
                "-4",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "240",
                "Peaceful",
                "We wish more people get to feel this way. Hope you can maintain your calm for as long as possible. Talk to friends you have not spoken to in a while. Your peace may help them feel a bit more peaceful as well.",
                R.drawable.relaxed,
                "3",
                "-4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "200",
                "Peeved",
                "Ignore people or things that are annoying you if you can. If you can't, try your best to keep your calm around them. Fun challenge: Observe your thoughts. Sometimes you will make interesting observations about your own mind. For example, you may find that your brain is getting worked up just because of its preferences. And that nobody is intentionally doing anything to annoy you.",
                R.drawable.irritated,
                "-1",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "201",
                "Perplexed",
                "It is hard. Be patient, stick to what you know, and do it confidently.",
                R.drawable.angry,
                "-1",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "129",
                "Persecuted",
                "Other people’s negative emotions will be out there and won’t always be planned. They will take their toll no matter what. Take the time to recover- Do nothing or make no plans. Find your “me” time to re-center and re-focus.",
                R.drawable.sad,
                "-3",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "93",
                "Pessimistic",
                "We feel pessimistic due to our bad experiences. Unfortunately, feeling that way for an extended period may come in the way of our growth and happiness. One way to feel less pessimistic is to remind ourselves of the good events of our life.",
                R.drawable.guilty,
                "-5",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "262",
                "Playful",
                "Who wouldn't want to be in your company right now? We hope you have a lot of fun doing things that you enjoy. Considering making some friends smile or shy by giving them compliments. ",
                R.drawable.naughty,
                "4",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "216",
                "Pleasant",
                "Aren't you lucky! Everyone would love to feel like this. Take a moment to feel gratitude in your heart for everything and everyone responsible for this.",
                R.drawable.relaxed,
                "1",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "217",
                "Pleased",
                "Glad you are feeling this way. Express gratitude in your mind or out loud to those who have made you feel this way. Journal the reasons on Onourem. Reading these reasons will help you later when you aren't feeling up to the mark.",
                R.drawable.satisfied,
                "1",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "237",
                "Powerful",
                "Glad that you are feeling powerful. Hope this doesn't grow your ego, and you stay grounded and compassionate.",
                R.drawable.relaxed,
                "2",
                "4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "104",
                "Powerless",
                "One of the toughest phases of life. But like all phases, this will pass too. Be patient. While this continues, engage in meaningful activities that give you satisfaction. Talk to friends who can understand your pain or read books that put life's phases in perspective.",
                R.drawable.sad,
                "-4",
                "-5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "160",
                "Pressured",
                "Life sometimes brings us out of our comfort zone. What matters is whether we can keep our calm and do our best given all the constraints. These situations are good opportunities for us to observe our thoughts. Are we thinking more about the possible outcomes or spending more energy doing the things we can. Consciously changing our thought patterns empowers us.",
                R.drawable.anxious,
                "-3",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "263",
                "Proud",
                "Enjoy the feeling. You are probably already working on this but just in case, make sure the pride doesn't boost your ego. Stay grounded and maintain humility. Know that many people and circumstances beyond your control have also contributed. Think of those and feel grateful.",
                R.drawable.naughty,
                "4",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "178",
                "Provoked",
                "Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. Later, don't forget to introspect if you would like your brain to react differently in the future. ",
                R.drawable.irritated,
                "-2",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "130",
                "Rejected",
                "Take your time to settle with the emotion. We all face rejection sometime or the other. What is most important is to not let rejection define us. Sometimes we could have done something differently, but sometimes there was really nothing we could have done. The best is to learn from it and move on. We can chose to start a new journey.",
                R.drawable.sad,
                "-3",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "75",
                "Relaxed",
                "Lovely! Enjoy the moment. Don't forget to note down on Onourem any specific incident that has made you feel this way. If you feel like it, check on friends.",
                R.drawable.relaxed,
                "1",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "133",
                "Remorseful",
                "The fact that you are feeling remorseful highlights the positive in you. It means that you care about how your actions impact others and that your ego isn't big enough to not recognize your mistake. If possible, express how you feel to those you may have hurt, intentionally or unintentionally. Introspect to identify how you can act differently in the future.",
                R.drawable.guilty,
                "-3",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "98",
                "Repelled",
                "That must be very uncomfortable. Not all situations or people value what we do. Sometimes we can do something, and sometimes we can't do anything to improve the situation. Use this opportunity to observe how your brain reacts to discomfort. If this feeling continues, engage in activities that can break the chain of negative emotions. Call a friend.",
                R.drawable.insecure,
                "-5",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "99",
                "Repulsed",
                "That must be very uncomfortable. Not all situations or people value what we do. Sometimes we can do something, and sometimes we can't do anything to improve the situation. Use this opportunity to observe how your brain reacts to discomfort. If this feeling continues, engage in activities that can break the chain of negative emotions. Call a friend.",
                R.drawable.angry,
                "-5",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "247",
                "Respected",
                "Such a great feeling! Make sure to not let your ego grow. Stay grounded, and be kind and compassionate. Use this positive mindset to build wholesome habits. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better.",
                R.drawable.satisfied,
                "3",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "241",
                "Restful",
                "We wish more people get to feel this way. Hope you can maintain your calm for as long as possible. Talk to friends you have not spoken to in a while. Your peace may help them feel a bit more peaceful as well.",
                R.drawable.relaxed,
                "3",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "85",
                "Restless",
                "Remember, you are not alone. Build a realistic timeline or a doable short-term goal to get a sense of accomplishment. A friend may help you see this situation in a less stressful way.",
                R.drawable.restless,
                "-1",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "95",
                "Revolted",
                "Acknowledge the feeling. Respond to the situation by understanding the root cause of the feeling. A friend may be able to help you look at the situation differently.",
                R.drawable.insecure,
                "-5",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "120",
                "Ridiculed",
                "An uncomfortable emotion to go through. You feeling ridiculed says more about those who made you feel this way. Share how you are feeling with a friend. If you have made a mistake, learn from it and move on.",
                R.drawable.lonely,
                "-4",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "161",
                "Rushed",
                "Life sometimes brings us out of our comfort zone. What matters is whether we can keep our calm and do our best given all the constraints. These situations are good opportunities for us to observe our thoughts.",
                R.drawable.anxious,
                "-3",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "66",
                "Sad",
                "Everyone feels this way once in a while. Take some time to understand the root cause of what makes you feel this way and see if there are better ways to look at things. Share how you are feeling with a friend.",
                R.drawable.sad,
                "-2",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "76",
                "Satisfied",
                "We wish more people get to feel this way. Hope you can maintain your calm for as long as possible. Talk to friends you have not spoken to in a while. Your peace may help them feel a bit more peaceful as well. Spend a few minutes feeling gratitude in your heart for all those who contribute to your life.",
                R.drawable.satisfied,
                "3",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "67",
                "Scared",
                "Take a few deep breaths. Apprehension about trying new things is general. Don't try to be perfect. A friend may help you see this situation from a less stressful perspective. Talk and share if possible. It is difficult to think clearly when we are afraid. Pray.",
                R.drawable.scared,
                "-2",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "226",
                "Secure",
                "A great feeling. We wish more people feel like this. If you feel like it, spend some time feeling gratitude for those who contribute to your life. ",
                R.drawable.satisfied,
                "2",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "145",
                "Sensitive",
                "Everyone feels this way once in a while. Take some time to understand the root cause of what makes you feel this way and see if there are better ways to look at things. Share how you are feeling with a friend.",
                R.drawable.confused,
                "-3",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "268",
                "Serene",
                "Enjoy the moment. We wish you get to feel this emotion often. You can enhance this experience by thinking of all the people who have contributed to your life. Feel grateful to them. If you feel like it, tell them you appreciate their contribution to your life.",
                R.drawable.relaxed,
                "5",
                "-5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "207",
                "Shocked",
                "Breathe. Take your time to let this phase pass. Take time to understand the incident better. Respond if you need to when you are calm. What will matter in the long run is how you respond to unexpected situations. Talking to a friend may help get a different perspective.",
                R.drawable.surprised,
                "-1",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "68",
                "Silly",
                "Wonderful. Not many people realize when they are being silly. Call a close friend.",
                R.drawable.silly,
                "2",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "146",
                "Skeptical",
                "Great that you know you are being skeptical. This emotion helps us safeguard our interests. But when we are skeptical in general, it limits our growth and experiences. So introspect, is there a past experience in this domain that warrants you to be skeptical in this situation?",
                R.drawable.tired,
                "-3",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "69",
                "Sleepy",
                "We hope you get to rest soon. But if this is happening even after you have had enough sleep, move around to let this pass. If this is happening very often, talk to a doctor to understand if you need to make any changes in diet or routine.",
                R.drawable.sleepy,
                "1",
                "-5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "134",
                "Sorry",
                "The fact that you are feeling sorry highlights the positive in you. It means that you care about how your actions impact others and that your ego isn't big enough to not recognize your mistake. If possible, express how you feel to those you may have hurt, intentionally or unintentionally. Introspect to identify how you can act differently in the future.",
                R.drawable.guilty,
                "-3",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "163",
                "Spent",
                "Kudos to all the efforts you made. Take a break if you can. If not, know that this hard work will make you feel satisfied once this is done. Breathe. Keep your calm and do what needs to be done. Make sure to rejuvenate as soon as you can.",
                R.drawable.tired,
                "-2",
                "-5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "208",
                "Startled",
                "Breathe. Take your time to let this phase pass. Take time to understand the incident better. Respond if you need to when you are calm. What will matter in the long run is how you respond to unexpected situations. Talking to a friend may help get a different perspective.",
                R.drawable.surprised,
                "-1",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "162",
                "Stressed",
                "Life sometimes brings us out of our comfort zone. What matters is whether we can keep our calm and do our best given all the constraints. These situations are good opportunities for us to observe our thoughts. Are we thinking more about the possible outcomes or spending more energy doing the things we can. Consciously changing our thought patterns empowers us. A friend may help you see this situation from a less stressful perspective.",
                R.drawable.tense,
                "-3",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "205",
                "Stunned",
                "Breathe. Take your time to let this phase pass. Take time to understand the incident better. Respond if you need to when you are calm. What will matter in the long run is how you respond to unexpected situations. Talking to a friend may help get a different perspective.",
                R.drawable.surprised,
                "-1",
                "4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "264",
                "Successful",
                "Enjoy the feeling. You are probably already working on this but just in case, make sure the pride doesn't boost your ego. Stay grounded and maintain humility. Know that many people and circumstances beyond your control have also contributed. Think of those and feel grateful.",
                R.drawable.naughty,
                "4",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "126",
                "Sullen",
                "Everyone feels this way once in a while. Take some time to understand the root cause of what makes you feel this way and see if there are better ways to look at things. Share how you are feeling with a friend.",
                R.drawable.sad,
                "-4",
                "-4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "78",
                "Surprised",
                "We hope you are pleasantly surprised. If not, see if you can use this opportunity to understand why it happened. Journal, what surprised you. You will enjoy reading it later.",
                R.drawable.surprised,
                "1",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "80",
                "Tense",
                "Take a few deep breaths. A friend may help you see this situation from a less stressful perspective. Talk and share if possible. It is difficult to think clearly when we are afraid. Pray.",
                R.drawable.tense,
                "-2",
                "4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "110",
                "Terrible",
                "A difficult emotion to go through. But, like all phases of life, this will pass too. Acknowledge the feeling, and cry if you feel like it. If possible, engage in activities that touch you at a deeper level. Talk to a friend who understands you, do something for the needy, or engage in an old hobby.",
                R.drawable.depressed,
                "-4",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "260",
                "Thankful",
                "Gratitude brings so many benefits to those who feel it. Use this moment to appreciate all those who have contributed to your life. If you have the energy, give some of them a call and appreciate them.",
                R.drawable.relaxed,
                "4",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "224",
                "Thoughtful",
                "Being thoughtful is great. We hope you are at ease and will keep adding value to people's lives.",
                R.drawable.relaxed,
                "2",
                "-4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "185",
                "Threatened",
                "Take a few deep breaths. A friend may help you see this situation from a less stressful perspective. Talk and share if possible.",
                R.drawable.scared,
                "-2",
                "4"
            )
        )
        expressionList.add(
            UserExpressionList(
                "274",
                "Thrilled",
                "One of those moments you will remember for life. Best wishes for your plans and goals. Have fun! A small challenge for you. Close your eyes and observe how your breath feels around your nostrils for 5 minutes without getting distracted.",
                R.drawable.excited,
                "5",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "70",
                "Tired",
                "We hope you get to rest soon. Kudos to all the efforts you made. Take a break if you can. If not, know that this hard work will make you feel satisfied once this is done. If you feel tired more often than you should, think about changing your diet and sleep patterns. Evaluate your thought patterns as well. Maybe you are stressed about something. Try to identify the root cause and address that.",
                R.drawable.tired,
                "-1",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "271",
                "Touched",
                "A great feeling. This must fill you with emotions of gratitude towards those who made you feel this way. If possible, express your appreciation to them. Share your experience with a friend. It subtly encourages similar behavior in them.",
                R.drawable.relaxed,
                "5",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "179",
                "Touchy",
                "Everyone feels this way once in a while. Let the moment pass. Avoid reacting. Breathe. Go out for a run or tear apart your old useless clothes but don't let this out on people around you. Later, don't forget to introspect if you would like your brain to react differently in such a situation. Share how you are feeling with a friend.",
                R.drawable.irritated,
                "-2",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "239",
                "Tranquil",
                "We wish more people get to feel this way. Hope you can maintain your calm for as long as possible. Talk to friends you have not spoken to in a while. Your peace may help them feel a bit more peaceful as well.",
                R.drawable.relaxed,
                "3",
                "-5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "117",
                "Troubled",
                "We appreciate that you acknowledged this feeling. Talk to a friend you trust. Share what is troubling you. A friend may help look at things from a different perspective.",
                R.drawable.irritated,
                "-4",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "71",
                "Undefined",
                "We will try to add more moods in the future to capture how you are feeling. Meanwhile, answer some questions, listen to some Vocals, or send some appreciation messages on Onourem. We are sure you will feel good.",
                R.drawable.confused,
                "0",
                "0"
            )
        )
        expressionList.add(
            UserExpressionList(
                "174",
                "Uneasy",
                "It is normal to feel this way sometimes. Fun challenge: Try taking your mind off the subject that makes you feel this way. Engage in activities that make you feel good. You may find it difficult to take your mind off. Observe your brain thought pattern. Know that this uneasy feeling will pass with time. Breathe through it.",
                R.drawable.confused,
                "-2",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "192",
                "Unfocused",
                "Glad you noticed. Most people don't even realize when they aren't able to focus. Take a break if you can. Plan and set specific times for specific tasks to feel calmer while working on one thing, knowing that you will have time later to address other matters.",
                R.drawable.worthless,
                "-1",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "238",
                "Upbeat",
                "Great! Enjoy the experience. Count your blessings and help others.",
                R.drawable.cheerful,
                "2",
                "5"
            )
        )
        expressionList.add(
            UserExpressionList(
                "248",
                "Valued",
                "Such a great feeling! Make sure to not let your ego grow. Stay grounded, and be kind and compassionate. Use this positive mindset to build wholesome habits. Don't forget to check on your friends once in a while. You never know who can use your positive energy to feel better.",
                R.drawable.satisfied,
                "3",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "167",
                "Victimised",
                "A painful feeling to go through. Find the strength to stay calm and look for ways to improve the situation. If possible, share how you are feeling with a friend. Seek support or professional advice if needed. Trust this will pass. Keep looking for a solution.",
                R.drawable.depressed,
                "-2",
                "-3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "156",
                "Violated",
                "Not everyone values what we do. Focus on whether someone really intended to hurt you, or it was just situational. When possible, learn, forgive and move on.",
                R.drawable.ashamed,
                "-3",
                "3"
            )
        )
        expressionList.add(
            UserExpressionList(
                "170",
                "Vulnerable",
                "Everyone feels like this one time or the other. If you are feeling too weak, just breathe. Like all phases of life, this will pass as well. When you have the energy, observe your thoughts to identify what you need to feel less fragile. Talk to a friend.",
                R.drawable.confused,
                "-2",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "113",
                "Weak",
                "We appreciate that you have the courage to accept how you are genuinely feeling. So many people deny such feelings and force them underground, where they can do more damage with time. Cry if you feel like it. Notice if you feel relief after the tears stop. Write down how and why you are feeling like this. Talk to a friend who understands you. ",
                R.drawable.sad,
                "-4",
                "-2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "175",
                "Weird",
                "It is normal to feel this way sometimes. Observe your thoughts to understand the root cause of this feeling. Use this opportunity to understand yourself better. See if you can do anything to improve the situation. Introspect how you would like to handle such situations in the future. All such uncomfortable experiences are a great way to train ourselves to deal with them.",
                R.drawable.confused,
                "-2",
                "1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "198",
                "Withdrawn",
                "A lot of people feel that way once in a while. If the feeling persists, try to understand the root cause. Is it because you are stressed? Or hurt? Or disappointed? Identify the reason behind the feeling and work on it.",
                R.drawable.tense,
                "-1",
                "-1"
            )
        )
        expressionList.add(
            UserExpressionList(
                "151",
                "Worried",
                "An emotion that keeps us vigilant to take the required action in an evolving situation. If you can't do anything, Pray! Provide your emotional support to those who may need it. Talk to someone you feel comfortable with.",
                R.drawable.tense,
                "-3",
                "2"
            )
        )
        expressionList.add(
            UserExpressionList(
                "82",
                "Worthless",
                "Sometimes, when you feel worthless, focusing your attention on something other than yourself can help. Helping others can also help you feel a greater sense of connection and purpose.",
                R.drawable.worthless,
                "-4",
                "-2"
            )
        )


        val response = Gson().fromJson(
            preferenceHelper.getString(Constants.CHANGED_MOOD),
            GetUserMoodResponseMsgResponse::class.java
        )


        expressionList.forEach { userExpression ->
            if (response != null && response.changedMessageList.isNotEmpty()) {
                response.changedMessageList.forEach {
                    if (userExpression.id == it.id) {
                        userExpression.expressionResponseMsg = it.expressionText
                    }
                }
            }
        }
        return expressionList
    }
}