package com.example.blacksquare.Singleton

class Definitions {
    companion object {

        const val APP_SHARED_PREFS: String = "APP SHARED PREFERENCES"
        /**
         * push notification keys
         */
        const val notificationTitleKey = "title"
        const val notificationBodyKey = "body"
        const val notificationImageKey = "media-attachment-url"
        const val notificationDeepLinkKey = "deeplink"
        const val notificationCTALabelKey = "call_to_action_label"
        const val notificationCancelLabelKey = "cancel_label"


        const val QUARTER_NOTE = 0
        const val EIGHT_NOTE = 1
        const val SIXTENTH_NOTE = 2
        const val THIRTY_TWO_NOTE = 3
        const val SIXTY_FOUR_NOTE = 4
        const val QUARTER_NOTE_TRIPLET = 5
        const val EIGHT_NOTE_TRIPLET = 6
        const val SIXTENTH_NOTE_TRIPLET = 7
        const val DOTTED_EIGHT_NOTE = 8


        /**
         *Drum pads
         */
        //DRUM PAD Id's//
        const val pad1Id = 1
        const val pad2Id = 2
        const val pad3Id = 3
        const val pad4Id = 4
        const val pad5Id = 5
        const val pad6Id = 6
        const val pad7Id = 7
        const val pad8Id = 8
        const val pad9Id = 9
        const val pad10Id = 10
        const val pad11Id = 11
        const val pad12Id = 12
        const val pad13Id = 13
        const val pad14Id = 14
        const val pad15Id = 15
        const val pad16Id = 16
        const val pad17Id = 17
        const val pad18Id = 18
        const val pad19Id = 19
        const val pad20Id = 20

        //Drum pad Index
        const val pad1Index: Int = 0
        const val pad2Index: Int = 1
        const val pad3Index: Int = 2
        const val pad4Index: Int = 3
        const val pad5Index: Int = 4
        const val pad6Index: Int = 5
        const val pad7Index: Int = 6
        const val pad8Index: Int = 7
        const val pad9Index: Int = 8
        const val pad10Index: Int = 9
        const val pad11Index: Int = 10
        const val pad12Index: Int = 11
        const val pad13Index: Int = 12
        const val pad14Index: Int = 13
        const val pad15Index: Int = 14
        const val pad16Index: Int = 15
        const val pad17Index: Int = 16
        const val pad18Index: Int = 17
        const val pad19Index: Int = 18
        const val pad20Index: Int = 19

        /**
         * Shared Prefs
         */

        // Drum pad volume keys
        const val padVolumeDefault = 0.75f
        const val pad1LftVolume: String = "pd1Lv"
        const val pad1RftVolume: String = "pd1Rv"
        const val pad2LftVolume: String = "pd2Lv"
        const val pad2RftVolume: String = "pd2Rv"
        const val pad3LftVolume: String = "pd3Lv"
        const val pad3RftVolume: String = "pd3Rv"
        const val pad4LftVolume: String = "pd4Lv"
        const val pad4RftVolume: String = "pd4Rv"
        const val pad5LftVolume: String = "pd5Lv"
        const val pad5RftVolume: String = "pd5Rv"
        const val pad6LftVolume: String = "pd6Lv"
        const val pad6RftVolume: String = "pd6Rv"
        const val pad7LftVolume: String = "pd7Lv"
        const val pad7RftVolume: String = "pd7Rv"
        const val pad8LftVolume: String = "pd8Lv"
        const val pad8RftVolume: String = "pd8Rv"
        const val pad9LftVolume: String = "pd9Lv"
        const val pad9RftVolume: String = "pd9Rv"
        const val pad10LftVolume: String = "pd10Lv"
        const val pad10RftVolume: String = "pd10Rv"

        //Metronome volume keys
        const val metronomeVolume: String ="metronome volume"


        /**
         * Pattern ID
         */

        const val pattern1: Int = 1
        const val pattern2: Int = 2
        const val pattern3: Int = 3
        const val pattern4: Int = 4
        const val pattern5: Int = 5
        const val pattern6: Int = 6
        const val pattern7: Int = 7
        const val pattern8: Int = 8
        const val pattern9: Int = 9
        const val pattern10: Int = 10
        const val pattern11: Int = 11
        const val pattern12: Int = 12

        /**
         * Bar measure value
         */

        const val oneBar: Int = 1
        const val twoBars: Int = 2
        const val fourBars: Int = 4
        const val eightBars: Int = 8

        /**
         * Bar measure index
         */
        const val oneBarIndex: Int = 0
        const val twoBarsIdex: Int = 1
        const val fourBarsIndex: Int = 2
        const val eightBarsIndex: Int = 3
    }
}