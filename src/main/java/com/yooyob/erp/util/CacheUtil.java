package com.yooyob.erp.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheUtil {

    private final CacheManager cacheManager;

    // Noms des caches
    public static final String CLIENT_CACHE = "clients";
    public static final String FOURNISSEUR_CACHE = "fournisseurs";
    public static final String FACTURE_CACHE = "factures";
    public static final String PRODUIT_CACHE = "produits";
    public static final String DEVISE_CACHE = "devises";
    public static final String TAXE_CACHE = "taxes";
    public static final String STATISTIQUE_CACHE = "statistiques";

    /**
     * Met en cache une valeur
     */
    public void put(String cacheName, Object key, Object value) {
        try {
            Cache cache = getCache(cacheName);
            if (cache != null) {
                cache.put(key, value);
                log.debug("Mise en cache - Cache: {}, Clé: {}", cacheName, key);
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la mise en cache - Cache: {}, Clé: {}", cacheName, key, e);
        }
    }

    /**
     * Récupère une valeur du cache
     */
    public <T> T get(String cacheName, Object key, Class<T> type) {
        try {
            Cache cache = getCache(cacheName);
            if (cache != null) {
                Cache.ValueWrapper wrapper = cache.get(key);
                if (wrapper != null) {
                    Object value = wrapper.get();
                    if (type.isInstance(value)) {
                        log.debug("Cache hit - Cache: {}, Clé: {}", cacheName, key);
                        return type.cast(value);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la récupération du cache - Cache: {}, Clé: {}", cacheName, key, e);
        }

        log.debug("Cache miss - Cache: {}, Clé: {}", cacheName, key);
        return null;
    }

    /**
     * Supprime une entrée du cache
     */
    public void evict(String cacheName, Object key) {
        try {
            Cache cache = getCache(cacheName);
            if (cache != null) {
                cache.evict(key);
                log.debug("Éviction du cache - Cache: {}, Clé: {}", cacheName, key);
            }
        } catch (Exception e) {
            log.warn("Erreur lors de l'éviction du cache - Cache: {}, Clé: {}", cacheName, key, e);
        }
    }

    /**
     * Vide complètement un cache
     */
    public void clear(String cacheName) {
        try {
            Cache cache = getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.debug("Cache vidé: {}", cacheName);
            }
        } catch (Exception e) {
            log.warn("Erreur lors du vidage du cache: {}", cacheName, e);
        }
    }

    /**
     * Vide tous les caches
     */
    public void clearAll() {
        try {
            Collection<String> cacheNames = cacheManager.getCacheNames();
            cacheNames.forEach(this::clear);
            log.info("Tous les caches ont été vidés");
        } catch (Exception e) {
            log.warn("Erreur lors du vidage de tous les caches", e);
        }
    }

    /**
     * Vérifie si une clé existe dans le cache
     */
    public boolean exists(String cacheName, Object key) {
        try {
            Cache cache = getCache(cacheName);
            return cache != null && cache.get(key) != null;
        } catch (Exception e) {
            log.warn("Erreur lors de la vérification d'existence dans le cache - Cache: {}, Clé: {}",
                    cacheName, key, e);
            return false;
        }
    }

    /**
     * Génère une clé de cache pour les statistiques
     */
    public String generateStatistiqueKey(String type, Object... params) {
        StringBuilder keyBuilder = new StringBuilder("stats_").append(type);
        for (Object param : params) {
            keyBuilder.append("_").append(param);
        }
        return keyBuilder.toString();
    }

    /**
     * Génère une clé de cache pour les requêtes paginées
     */
    public String generatePageKey(String baseKey, int page, int size, String sortBy) {
        return String.format("%s_page_%d_size_%d_sort_%s", baseKey, page, size, sortBy);
    }

    /**
     * Génère une clé de cache pour les filtres
     */
    public String generateFilterKey(String baseKey, Object... filters) {
        StringBuilder keyBuilder = new StringBuilder(baseKey).append("_filter");
        for (Object filter : filters) {
            if (filter != null) {
                keyBuilder.append("_").append(filter.toString().replaceAll("[^a-zA-Z0-9]", ""));
            }
        }
        return keyBuilder.toString();
    }

    /**
     * Met en cache les données d'un client
     */
    public void cacheClient(UUID clientId, Object client) {
        put(CLIENT_CACHE, clientId, client);
    }

    /**
     * Récupère les données d'un client du cache
     */
    public <T> T getCachedClient(UUID clientId, Class<T> type) {
        return get(CLIENT_CACHE, clientId, type);
    }

    /**
     * Évince un client du cache
     */
    public void evictClient(UUID clientId) {
        evict(CLIENT_CACHE, clientId);
    }

    /**
     * Met en cache les données d'une facture
     */
    public void cacheFacture(UUID factureId, Object facture) {
        put(FACTURE_CACHE, factureId, facture);
    }

    /**
     * Récupère les données d'une facture du cache
     */
    public <T> T getCachedFacture(UUID factureId, Class<T> type) {
        return get(FACTURE_CACHE, factureId, type);
    }

    /**
     * Évince une facture du cache
     */
    public void evictFacture(UUID factureId) {
        evict(FACTURE_CACHE, factureId);
        // Évince aussi les statistiques qui pourraient être affectées
        clearStatistiques();
    }

    /**
     * Met en cache les données d'un produit
     */
    public void cacheProduit(UUID produitId, Object produit) {
        put(PRODUIT_CACHE, produitId, produit);
    }

    /**
     * Récupère les données d'un produit du cache
     */
    public <T> T getCachedProduit(UUID produitId, Class<T> type) {
        return get(PRODUIT_CACHE, produitId, type);
    }

    /**
     * Évince un produit du cache
     */
    public void evictProduit(UUID produitId) {
        evict(PRODUIT_CACHE, produitId);
    }

    /**
     * Vide le cache des statistiques
     */
    public void clearStatistiques() {
        clear(STATISTIQUE_CACHE);
    }

    /**
     * Invalide les caches liés à un client (factures, paiements, etc.)
     */
    public void invalidateClientRelatedCaches(UUID clientId) {
        evictClient(clientId);
        clearStatistiques();

        // Évince les factures liées au client (approximation)
        // Dans un vrai système, on devrait maintenir un index des relations
        log.debug("Invalidation des caches liés au client: {}", clientId);
    }

    /**
     * Invalide les caches lors de la création/modification d'une facture
     */
    public void invalidateFactureRelatedCaches(UUID factureId, UUID clientId) {
        evictFacture(factureId);
        if (clientId != null) {
            evictClient(clientId);
        }
        clearStatistiques();
        log.debug("Invalidation des caches liés à la facture: {}", factureId);
    }

    /**
     * Obtient une instance de cache
     */
    private Cache getCache(String cacheName) {
        try {
            return cacheManager.getCache(cacheName);
        } catch (Exception e) {
            log.warn("Impossible d'obtenir le cache: {}", cacheName, e);
            return null;
        }
    }

    /**
     * Obtient des informations sur l'état des caches
     */
    public void logCacheInfo() {
        try {
            Collection<String> cacheNames = cacheManager.getCacheNames();
            log.info("Caches disponibles: {}", cacheNames);

            for (String cacheName : cacheNames) {
                Cache cache = getCache(cacheName);
                if (cache != null) {
                    log.info("Cache '{}' - Type: {}", cacheName, cache.getClass().getSimpleName());
                }
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la récupération des informations de cache", e);
        }
    }
}