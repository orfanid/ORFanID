package com.orfangenes.util;

public class Database {
    //Databases
    public static final String HOST = "127.0.0.1";
    public static final String PORT = "3306";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "root";
    public static final String DB_ORFAN = "orfandb";

    //Tables
    public static final String TB_ORFAN_GENES = "orfanGenes";
    public static final String TB_STRICT_ORFANS = "strictOrfans";
    public static final String TB_MD_GENES = "multiDomainGenes";
    public static final String TB_DOMAIN_RG = "domanRestrictedGenes";
    public static final String TB_KINGDOM_RG = "kingdomRestrictedGenes";
    public static final String TB_PHYLUM_RG = "phylumRestrictedGenes";
    public static final String TB_CLASS_RG = "classRestrictedGenes";
    public static final String TB_ORDER_RG = "orderRestrictedGenes";
    public static final String TB_FAMILY_RG = "familyRestrictedGenes";
    public static final String TB_GENUS_RG = "genusRestrictedGene";
}
