package ImGui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredefinedStrings {



    public static List<String> predefinedNames = List.of(
            "Venator remains",
            "Legionary remains",
            "Castra debris",
            "Lodge bar storage",
            "Lodge art storage",
            "Administratum debris",
            "Cultist footlocker",
            "Sacrificial altar",
            "Prodromoi remains",
            "Dis dungeon debris",
            "Praesidio remains",
            "Monoceros remains",
            "Amphitheatre debris",
            "Ceramics studio debris",
            "Carcerem debris",
            "Ministry remains",
            "Stadio debris",
            "Cathedral debris",
            "Marketplace debris",
            "Inquisitor remains",
            "Infernal art",
            "Gladiator remains",
            "Citizen remains",
            "Shakroth remains",
            "Dominion Games podium",
            "Ikovian memorial",
            "Dragonkin remains",
            "Oikos studio debris",
            "Kharid-et chapel debris",
            "Forum entrance",
            "Gladiatorial goblin remains",
            "Keshik ger",
            "Animal trophies",
            "Pontifex remains",
            "Crucible stands debris",
            "Tailory debris",
            "Goblin dorm debris",
            "Oikos fishing hut remnants",
            "Weapons research debris",
            "Orcus altar",
            "Standing stone debris",
            "Runic debris",
            "Dis overspill",
            "Big High War God shrine",
            "Orthen rubble",
            "Varanusaur remains",
            "Gravitron research debris",
            "Acropolis debris",
            "Armarium debris",
            "Yu'biusk animal pen",
            "Keshik tower debris",
            "Dragonkin reliquary",
            "Goblin trainee remains",
            "Byzroth remains",
            "Destroyed golem",
            "Dragonkin coffin",
            "Icyene weapon rack",
            "Culinarum debris",
            "Kyzaj champion's boudoir",
            "Autopsy table",
            "Experiment workbench",
            "Keshik weapon rack",
            "Hellfire forge",
            "Warforge scrap pile",
            "Stockpiled art",
            "Aughra remains",
            "Ancient magick munitions",
            "Moksha device",
            "Bibliotheke debris",
            "Chthonian trophies",
            "Warforge weapon rack",
            "Flight research debris",
            "Aetherium forge",
            "Xolo mine",
            "Praetorian remains",
            "Bandos's sanctum debris",
            "Tsutsaroth remains",
            "Optimatoi remains",
            "War table debris",
            "Howl's workshop debris",
            "Makeshift pie oven",
            "Xolo remains"
    );
    public static List<String> predefinedCacheNames = List.of(
            "Material cache (third Age iron)",
            "Material cache (Zarosian insignia)",
            "Material cache (samite silk)",
            "Material cache (imperial steel)",
            "Material cache (white oak)",
            "Material cache (goldrune)",
            "Material cache (orthenglass)",
            "Material cache (vellum)",
            "Material cache (cadmium red)",
            "Material cache (ancient vis)",
            "Material cache (Tyrian purple)",
            "Material cache (leather scraps)",
            "Material cache (chaotic brimstone)",
            "Material cache (demonhide)",
            "Material cache (Eye of Dagon)",
            "Material cache (hellfire metal)",
            "Material cache (keramos)",
            "Material cache (white marble)",
            "Material cache (cobalt blue)",
            "Material cache (Everlight silvthril)",
            "Material cache (Star of Saradomin)",
            "Material cache (Blood of Orcus)",
            "Material cache (soapstone)",
            "Material cache (Stormguard steel)",
            "Material cache (Wings of War)",
            "Material cache (animal furs)",
            "Material cache (Armadylean yellow)",
            "Material cache (malachite green)",
            "Material cache (Mark of the Kyzaj)",
            "Material cache (vulcanised rubber)",
            "Material cache (warforged bronze)",
            "Material cache (fossilised bone)",
            "Material cache (Yu'biusk clay)",
            "Material cache (aetherium alloy)",
            "Material cache (compass rose)",
            "Material cache (felt)",
            "Material cache (quintessence)",
            "Material cache (dragon metal)",
            "Material cache (carbon black)"
    );
    public static List<String> MiningList = List.of(
            "Light animica rock",
            "Dark animica rock",
            "Banite rock",
            "Orichalcite rock",
            "Drakolith rock",
            "Necrite rock",
            "Phasmatite rock",
            "Luminite rock",
            "Runite rock",
            "Adamantite rock",
            "Mithril rock",
            "Iron rock",
            "Tin rock",
            "Copper rock",
            "Soft clay rock",
            "Crystal-flecked sandstone",
            "Prifddinas gem rock"
    );

    public static List<String> TreeList = List.of(
            "Tree",
            "Oak",
            "Willow",
            "Maple",
            "Magic",
            "Yew",
            "Elder",
            "Mahogany",
            "Teak"
    );
    public static List<String> CombatList = List.of(
            "Goblin",
            "Zombie",
            "Skeleton",
            "Giant Spider",
            "Hill Giant",
            "Abyssal Demon"

    );
    public static List<String> FoodList = List.of(
            "Shark",
            "Rocktail",
            "Salmon",
            "Trout",
            "Swordfish",
            "Lobster"

    );
    public static List<String> LootList = List.of(
            "Charms",
            "Coins"


    );

    public static List<String> spiritStone = List.of(
            "Spirit onyx (a)",
            "Spirit dragonstone (a)",
            "Spirit diamond (a)",
            "Spirit ruby (a)",
            "Spirit emerald (a)",
            "Spirit sapphire (a)"
    );

    public static List<String> pouchName = List.of(
            "Geyser titan pouch"
    );
    static Map<Integer, String> secondaryItemName = new HashMap<>();
    static {
        secondaryItemName.put(1444, "Water talisman");
    }
}
