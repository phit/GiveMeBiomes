package me.phit.gmb;

public class BiomeColors {
    public static int getMapColor(String name) {
        int color = 105105105;
        switch (name) {
            // Vanilla Biomes 1.10.2
            case "Ocean":
                color = 5214719;
                break;
            case "Plains":
                color = 9286496;
                break;
            case "Desert":
                color = 16421912;
                break;
            case "Extreme Hills":
                color = 6316128;
                break;
            case "Forest":
                color = 353825;
                break;
            case "Taiga":
                color = 747097;
                break;
            case "Swampland":
                color = 522674;
                break;
            case "River":
                color = 255;
                break;
            case "Hell":
                color = 16711680;
                break;
            case "The End":
                color = 8421631;
                break;
            case "FrozenOcean":
                color = 9474208;
                break;
            case "FrozenRiver":
                color = 10526975;
                break;
            case "Ice Plains":
                color = 16777215;
                break;
            case "Ice Mountains":
                color = 10526880;
                break;
            case "MushroomIsland":
                color = 13990520;
                break;
            case "MushroomIslandShore":
                color = 15038581;
                break;
            case "Beach":
                color = 16440917;
                break;
            case "DesertHills":
                color = 13786898;
                break;
            case "ForestHills":
                color = 2250012;
                break;
            case "TaigaHills":
                color = 1456435;
                break;
            case "Extreme Hills Edge":
                color = 7501978;
                break;
            case "Jungle":
                color = 5470985;
                break;
            case "JungleHills":
                color = 2900485;
                break;
            case "JungleEdge":
                color = 5470985;
                break;
            case "Deep Ocean":
                color = 17588;
                break;
            case "Stone Beach":
                color = 555058;
                break;
            case "Cold Beach":
                color = 6124100;
                break;
            case "Birch Forest":
                color = 1477189;
                break;
            case "Birch Forest Hills":
                color = 1476781;
                break;
            case "Roofed Forest":
                color = 483584;
                break;
            case "Cold Taiga":
                color = 1456435;
                break;
            case "Cold Taiga Hills":
                color = 1456435;
                break;
            case "Mega Taiga":
                color = 1456435;
                break;
            case "Mega Taiga Hills":
                color = 1456435;
                break;
            case "Extreme Hills+":
                color = 6316128;
                break;
            case "Savanna":
                color = 14405453;
                break;
            case "Savanna Plateau":
                color = 14405453;
                break;
            case "Mesa":
                color = 16744492;
                break;
            case "Mesa Plateau F":
                color = 14838814;
                break;
            case "Mesa Plateau":
                color = 14838814;
                break;
            case "The Void":
                color = 11675067;
                break;
            case "Sunflower Plains":
                color = 10671971;
                break;
            case "Desert M":
                color = 16421912;
                break;
            case "Extreme Hills M":
                color = 6316128;
                break;
            case "Flower Forest":
                color = 353825;
                break;
            case "Taiga M":
                color = 1456435;
                break;
            case "Swampland M":
                color = 522674;
                break;
            case "Ice Plains Spikes":
                color = 5237737;
                break;
            case "Jungle M":
                color = 5470985;
                break;
            case "JungleEdge M":
                color = 5470985;
                break;
            case "Birch Forest M":
                color = 1477189;
                break;
            case "Birch Forest Hills M":
                color = 1477189;
                break;
            case "Roofed Forest M":
                color = 483584;
                break;
            case "Cold Taiga M":
                color = 1456435;
                break;
            case "Mega Spruce Taiga":
                color = 1456435;
                break;
            case "Redwood Taiga Hills M":
                color = 1456435;
                break;
            case "Extreme Hills+ M":
                color = 6316128;
                break;
            case "Savanna M":
                color = 14405453;
                break;
            case "Savanna Plateau M":
                color = 14405453;
                break;
            case "Mesa (Bryce)":
                color = 13268799;
                break;
            case "Mesa Plateau F M":
                color = 14838814;
                break;
            case "Mesa Plateau M":
                color = 14838814;
                break;
            default: // do nothing;
                break;
        }
        return color;
    }
}
