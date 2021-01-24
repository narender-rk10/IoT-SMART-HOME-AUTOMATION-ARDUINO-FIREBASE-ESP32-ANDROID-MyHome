package com.example.myhome.faq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> faq1 = new ArrayList<String>();
        faq1.add("The term smart home refers to the regular home, which has its appliances, lighting, and electronic devices connected to the internet to enable remote monitoring and management. The homeowner enjoys the convenience of securing, operating, and monitoring their house even from another side of the world. The devices that are connected are lights, electricity , water and even plants.");

        List<String> faq2 = new ArrayList<String>();
        faq2.add("Not at all. Yes your home or business will be high-tech but your life will be streamlined and more secure. Everything is controlled from a wall-mounted keypad, hand-held remote, tablet or smartphone, all labeled with the tasks each can accomplish with the touch of your finger. Imagine you are rushing out the door for work: Simply pressing “Away” locks all your doors, alarms the security system, turns off the lights and non-essential devices, and sets your thermostat to an away setting, saving precious energy – not to mention your valuable time.");

        List<String> faq3 = new ArrayList<String>();
        faq3.add("The first and foremost reason to buy a home automation system is for comfort and security. The other benefits that the homeowner gets to enjoy are the convenience, remote access, savings, and peace of mind. A few home automation systems have become more accessible and advanced, that the homeowners across the country are upgrading their homes.");

        List<String> faq4 = new ArrayList<String>();
        faq4.add("There are a variety of devices and appliances that get connected and controlled in a smart home setup. The most common ones fall into four categories, and they are lighting, climate control, electricity, water tank and plant.");

        List<String> faq5 = new ArrayList<String>();
        faq5.add("Yes, smart homes are affordable. However, there was a time when smart homes were considered a luxury. Setting up your smart home is easier than ever but choosing the right smart home system to unify your devices and get them talking together is the biggest challenge that a homeowner faces today. Know how you can benefit from turning your house into a smart home; this helps zero-in on the right product.");

        List<String> faq6 = new ArrayList<String>();
        faq6.add("Home automation is on high demand, and thereby, it increases home value. Today, everyone wants to live in a smart home and enjoy advanced technologies. When a house uses smart technology, the homeowner can most likely raise their asking price. It would be best if the homeowner worked with a licensed real estate agent, though, to ensure your price falls within what the market can bear.");

        List<String> faq7 = new ArrayList<String>();
        faq7.add("The best time to install home automation is now. The smart tech offers security and safety for the family and the house - nothing beats the security aspects. Besides, the best time to install home automation is while building a new home or renovating an existing one. In both the scenarios, the homeowners can get down to the wires easily.");

        List<String> faq8 = new ArrayList<String>();
        faq8.add("A variety of smart home devices and systems are currently available on the market. Each of these come with a unique set of features and different specifications. You may get confused about what to choose with so many options. Make sure that the gadgets you add are compatible with each other or just go ahead and get yourself a smart hub that will bridge any gaps.");

        List<String> faq9 = new ArrayList<String>();
        faq9.add("Making your whole house smart is NOT necessary. You first have to prioritize all your options and then decide whether to invest in a full-scale home automation network or in separate smart gadgets.\n" +
                "\n" +
                "If you want a automate your whole house, there are tons of home automation solutions you can choose from and these include appliances, climate control devices, access control and security elements, entertainment technology, lighting controls, and energy management tools to name a few. Also, you need not buy all the smart home devices you need at one shot. To help you stay within a planned budget, you can plan your purchases in different stages.\n" +
                "\n" +
                "No matter how you plan to buy the home automation systems or devices, always make sure that you choose your devices carefully because not all devices are compatible. You need to verify that the devices or systems you want to buy actually work together.");

        List<String> faq10 = new ArrayList<String>();
        faq10.add("Basic home automation systems will transmit a command out and just believe that it is actioned. However, more complex home automation systems will first send a command and then wait for a response. A variety of actions can be executed if a response is not approaching. Home automation systems are reliable but if you go in for the more complex devices and systems then the cost also becomes higher along with the need for a skilled specialist for the installation process.");

        expandableListDetail.put("What is a 'smart home'?", faq1);
        expandableListDetail.put("Is the smart home system complicated to use?", faq2);
        expandableListDetail.put("What are the benefits of home automation?", faq3);
        expandableListDetail.put("Which devices are most commonly controlled in a smart home?", faq4);
        expandableListDetail.put("Are smart homes affordable?", faq5);
        expandableListDetail.put("Does home automation increase home value?", faq6);
        expandableListDetail.put("When is the best time to install home automation?", faq7);
        expandableListDetail.put("What home automation systems are available today and what products do they work with?", faq8);
        expandableListDetail.put("Do I have to make my whole house smart?", faq9);
        expandableListDetail.put("How reliable is a home automation system?", faq10);

        return expandableListDetail;
    }
}
