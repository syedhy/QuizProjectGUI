package com.quizapp.profiles;

import java.util.ArrayList;
import java.util.List;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.filters.FluentFilter;
import org.dizitart.no2.mvstore.MVStoreModule;

import com.quizapp.data.AppData;

public class ProfileManager {

    private static Nitrite openDatabase() {
        AppData.ensureGeneratedDataFolder();

        MVStoreModule storeModule = MVStoreModule.withConfig()
                .filePath(AppData.DB_FILE.toString())
                .build();

        return Nitrite.builder().loadModule(storeModule).openOrCreate();
    }

    private static NitriteCollection getCollection(Nitrite db) {
        return db.getCollection("profiles");
    }

    public static void createProfile(String name) {
        try (Nitrite db = openDatabase()) {
            NitriteCollection collection = getCollection(db);

            Document existing = collection.find(FluentFilter.where("name").eq(name)).firstOrNull();

            if (existing != null) return;

            collection.insert(toDocument(new Profile(name)));
        }
    }

    public static Profile getProfile(String name) {
        try (Nitrite db = openDatabase()) {
            NitriteCollection collection = getCollection(db);

            Document document = collection.find(FluentFilter.where("name").eq(name)).firstOrNull();

            if (document == null) return null;

            return fromDocument(document);
        }
    }

    public static List<Profile> getAllProfiles() {
        List<Profile> profiles = new ArrayList<>();

        try (Nitrite db = openDatabase()) {
            NitriteCollection collection = getCollection(db);

            for (Document document : collection.find()) {
                profiles.add(fromDocument(document));
            }
        }

        return profiles;
    }

    public static void saveProfile(Profile profile) {
        if (profile == null || profile.getName().equalsIgnoreCase("Guest")) return;

        try (Nitrite db = openDatabase()) {
            NitriteCollection collection = getCollection(db);

            collection.remove(FluentFilter.where("name").eq(profile.getName()));
            collection.insert(toDocument(profile));
        }
    }

    public static void deleteProfile(String name) {
        if (name == null || name.isBlank() || name.equalsIgnoreCase("Guest")) return;

        try (Nitrite db = openDatabase()) {
            NitriteCollection collection = getCollection(db);
            collection.remove(FluentFilter.where("name").eq(name));
        }
    }

    private static Document toDocument(Profile profile) {
        return Document.createDocument("name" , profile.getName())
                .put("elo" , profile.getElo())
                .put("rankedGames" , profile.getRankedGames())
                .put("rankedWins" , profile.getRankedWins())
                .put("rankedLosses" , profile.getRankedLosses())
                .put("rankedTotalScore" , profile.getRankedTotalScore())
                .put("rankedBestScore" , profile.getRankedBestScore())
                .put("totalGames" , profile.getTotalGames())
                .put("totalQuestions" , profile.getTotalQuestions())
                .put("totalCorrect" , profile.getTotalCorrect())
                .put("timedGames" , profile.getTimedGames())
                .put("survivalGames" , profile.getSurvivalGames())
                .put("suddenDeathGames" , profile.getSuddenDeathGames())
                .put("pvpGames" , profile.getPvpGames())
                .put("llmGames" , profile.getLlmGames())
                .put("eloGames" , profile.getEloGames());
    }

    private static Profile fromDocument(Document document) {
        Profile profile = new Profile(document.get("name" , String.class));

        profile.setElo(getInt(document , "elo" , 1000));
        profile.setRankedGames(getInt(document , "rankedGames" , 0));
        profile.setRankedWins(getInt(document , "rankedWins" , 0));
        profile.setRankedLosses(getInt(document , "rankedLosses" , 0));
        profile.setRankedTotalScore(getInt(document , "rankedTotalScore" , 0));
        profile.setRankedBestScore(getInt(document , "rankedBestScore" , 0));
        profile.setTotalGames(getInt(document , "totalGames" , 0));
        profile.setTotalQuestions(getInt(document , "totalQuestions" , 0));
        profile.setTotalCorrect(getInt(document , "totalCorrect" , 0));
        profile.setTimedGames(getInt(document , "timedGames" , 0));
        profile.setSurvivalGames(getInt(document , "survivalGames" , 0));
        profile.setSuddenDeathGames(getInt(document , "suddenDeathGames" , 0));
        profile.setPvpGames(getInt(document , "pvpGames" , 0));
        profile.setLlmGames(getInt(document , "llmGames" , 0));
        profile.setEloGames(getInt(document , "eloGames" , 0));

        return profile;
    }

    private static int getInt(Document document , String key , int defaultValue) {
        Integer value = document.get(key , Integer.class);

        if (value == null) return defaultValue;

        return value;
    }
}