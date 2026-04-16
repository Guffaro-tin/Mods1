package com.tuonome.devilfruitmod.fruit;

/**
 * Enum che definisce i 3 frutti del diavolo con le loro abilità.
 * Ispirato alle abilità di Mine Mine no Mi, riscritto da zero per 1.20.1
 */
public enum DevilFruit {

    // ─── MAGU MAGU NO MI (Frutto del Magma) ────────────────────
    // Logia: immunità al fuoco, danno al contatto, lava flow, meteor
    MAGU("magu_magu_no_mi", "§cMagu Magu no Mi", "§7Frutto del Magma",
        new String[]{
            "§e► [R] Logia §7- Immunità fuoco + danno contatto",
            "§e► [1] Lava Flow §7- Crea lava sotto i piedi",
            "§e► [2] Magma Coating §7- Armatura di magma temporanea",
            "§e► [3] Ryusei Kazan §7- Pioggia di meteoriti"
        },
        FruitType.LOGIA,
        "FIRE_TYPE"
    ),

    // ─── HIE HIE NO MI (Frutto del Ghiaccio) ───────────────────
    // Logia: immunità freddo, congela nemici, frost walker, ice age
    HIE("hie_hie_no_mi", "§bHie Hie no Mi", "§7Frutto del Ghiaccio",
        new String[]{
            "§e► [R] Logia §7- Immunità ghiaccio + congela al contatto",
            "§e► [1] Frost Walker §7- Cammina sull'acqua congelando",
            "§e► [2] Ice Saber §7- Spada di ghiaccio +danno",
            "§e► [3] Ice Age §7- Congela tutto attorno a te"
        },
        FruitType.LOGIA,
        "ICE_TYPE"
    ),

    // ─── GORO GORO NO MI (Frutto del Fulmine) ──────────────────
    // Logia: immunità fulmine, velocità, elettricità al contatto
    GORO("goro_goro_no_mi", "§eGoro Goro no Mi", "§7Frutto del Fulmine",
        new String[]{
            "§e► [R] Logia §7- Immunità fulmine + shock al contatto",
            "§e► [1] Spark Step §7- Teletrasporto fulmineo",
            "§e► [2] El Thor §7- Fulmine dal cielo sul bersaglio",
            "§e► [3] Raigo §7- Sfera di fulmine gigante"
        },
        FruitType.LOGIA,
        "LIGHTNING_TYPE"
    );

    public enum FruitType { LOGIA, PARAMECIA, ZOAN }

    public final String itemId;
    public final String displayName;
    public final String subtitle;
    public final String[] abilityDescriptions;
    public final FruitType type;
    public final String damageTag;  // Integrazione con RaceMod

    DevilFruit(String itemId, String displayName, String subtitle,
               String[] abilityDescriptions, FruitType type, String damageTag) {
        this.itemId = itemId;
        this.displayName = displayName;
        this.subtitle = subtitle;
        this.abilityDescriptions = abilityDescriptions;
        this.type = type;
        this.damageTag = damageTag;
    }
}
