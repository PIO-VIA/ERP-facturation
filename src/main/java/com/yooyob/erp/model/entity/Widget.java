package com.yooyob.erp.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UserDefinedType("widget")
public class Widget {

    @Column("id_widget")
    private UUID idWidget;

    @Column("nom_widget")
    private String nomWidget;

    @Column("type_widget")
    private String typeWidget; // "KPI", "GRAPHIQUE", "TABLEAU", "JAUGE", "CALENDRIER"

    @Column("sous_type")
    private String sousType; // "BAR", "LINE", "PIE", "DONUT", "AREA", etc.

    @Column("titre")
    private String titre;

    @Column("description")
    private String description;

    @Column("position_x")
    private Integer positionX;

    @Column("position_y")
    private Integer positionY;

    @Column("largeur")
    private Integer largeur;

    @Column("hauteur")
    private Integer hauteur;

    @Column("source_donnees")
    private String sourceDonnees; // "FACTURES", "PAIEMENTS", "CLIENTS", etc.

    @Column("requete_sql")
    private String requeteSql;

    @Column("parametres_graphique")
    private Map<String, Object> parametresGraphique;

    @Column("colonnes_affichees")
    private List<String> colonnesAffichees;

    @Column("filtres_widget")
    private Map<String, Object> filtresWidget;

    @Column("agregations")
    private Map<String, String> agregations; // "SUM", "AVG", "COUNT", etc.

    @Column("groupement")
    private List<String> groupement;

    @Column("tri")
    private Map<String, String> tri; // colonne -> "ASC"/"DESC"

    @Column("limite_resultats")
    private Integer limiteResultats;

    @Column("couleurs_personnalisees")
    private List<String> couleursPersonnalisees;

    @Column("format_affichage")
    private Map<String, String> formatAffichage;

    @Column("unite_mesure")
    private String uniteMesure;

    @Column("precision_decimale")
    private Integer precisionDecimale;

    @Column("seuils_alertes")
    private Map<String, Object> seuilsAlertes;

    @Column("actions_clic")
    private Map<String, Object> actionsClic;

    @Column("visible")
    @Builder.Default
    private Boolean visible = true;

    @Column("actualisation_auto")
    @Builder.Default
    private Boolean actualisationAuto = true;

    @Column("cache_active")
    @Builder.Default
    private Boolean cacheActive = true;

    @Column("ordre_affichage")
    private Integer ordreAffichage;

    @Column("responsif")
    @Builder.Default
    private Boolean responsif = true;

    @Column("exportable")
    @Builder.Default
    private Boolean exportable = true;

    @Column("interactif")
    @Builder.Default
    private Boolean interactif = true;

    @Column("donnees_temps_reel")
    @Builder.Default
    private Boolean donneesTempsReel = false;

    @Column("websocket_channel")
    private String websocketChannel;
}