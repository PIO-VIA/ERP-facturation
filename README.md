# 🏢 ERP Facturation - Module de Facturation Avancé

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Cassandra](https://img.shields.io/badge/Cassandra-NoSQL-blue.svg)](https://cassandra.apache.org/)
[![Redis](https://img.shields.io/badge/Redis-Cache-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-Private-lightgrey.svg)]()

## 📋 Table des Matières

- [📖 Description](#-description)
- [🚀 Fonctionnalités](#-fonctionnalités)
- [🏗️ Architecture](#️-architecture)
- [💻 Technologies](#-technologies)
- [⚙️ Installation](#️-installation)
- [🔧 Configuration](#-configuration)
- [📚 API Documentation](#-api-documentation)
- [🧪 Tests](#-tests)
- [📦 Déploiement](#-déploiement)
- [🔒 Sécurité](#-sécurité)
- [📊 Monitoring](#-monitoring)
- [🤝 Contribution](#-contribution)

## 📖 Description

**ERP Facturation** est un système de gestion de facturation d'entreprise complet développé avec Spring Boot et Cassandra. Ce module offre une solution robuste pour la gestion des factures, devis, paiements, et bien plus encore.

### 🎯 Objectifs du Projet

- **Digitalisation** : Automatiser complètement le processus de facturation
- **Scalabilité** : Architecture microservices ready avec Cassandra NoSQL
- **Performance** : Optimisé avec Redis pour le cache et les performances
- **Compliance** : Respect des réglementations fiscales et comptables
- **UX/UI** : Interface moderne et intuitive pour les utilisateurs

## 🚀 Fonctionnalités

### 📄 Gestion des Documents

- **✅ Factures**
    - Création, modification, suppression
    - Statuts avancés (brouillon, envoyée, payée, annulée)
    - Génération PDF automatique
    - Envoi par email automatisé
    - Numérotation automatique configurable

- **✅ Devis**
    - Création et gestion des devis
    - Conversion automatique devis → facture
    - Suivi des approbations clients
    - Dates d'expiration et relances

- **✅ Avoirs/Notes de Crédit**
    - Gestion complète des remboursements
    - Workflow d'approbation
    - Traçabilité des motifs
    - Application partielle ou totale

### 💰 Gestion Financière

- **✅ Paiements**
    - Multi-devises avec conversion automatique
    - Différents modes de paiement
    - Rapprochement bancaire
    - Échéanciers personnalisables

- **✅ Escomptes et Remises**
    - Calcul automatique d'escompte pour paiement anticipé
    - Configurations flexibles par client/produit
    - Remises en cascade et promotions

- **✅ Multi-devises**
    - Support de multiples devises
    - Taux de change automatiques et historiques
    - Conversion temps réel
    - Alertes de variation des taux

### 🔄 Automatisation Avancée

- **✅ Facturation Récurrente/Abonnements**
    - Facturation automatique par abonnement
    - Gestion des fréquences (mensuelle, trimestrielle, annuelle)
    - Suspension et résiliation automatique
    - Gestion des échecs de paiement

- **✅ Workflow d'Approbation**
    - Circuits d'approbation configurables
    - Approbations multi-niveaux
    - Notifications automatiques
    - Historique complet des validations

- **✅ Relances Automatiques**
    - Planification intelligente des relances
    - Templates personnalisables
    - Escalade automatique
    - Statistiques de performance

### 📊 Analytics et Reporting

- **✅ Tableaux de Bord**
    - Dashboard exécutif temps réel
    - Indicateurs de performance (KPIs)
    - Graphiques interactifs
    - Exports personnalisables

- **✅ Analytics Avancées**
    - Analyse des tendances de vente
    - Performance par client/produit
    - Prévisions de revenus
    - Détection d'anomalies

- **✅ Rapports**
    - Rapports financiers automatisés
    - Exportation multi-formats (PDF, Excel, CSV)
    - Planification et envoi automatique
    - Rapports réglementaires

### 🔧 Configuration et Paramétrage

- **✅ Numérotation Avancée**
    - Schémas de numérotation personnalisables
    - Reset automatique (annuel/mensuel)
    - Préfixes par type de document
    - Validation et unicité

- **✅ Taxes et Fiscalité**
    - Gestion multi-taxes (TVA, taxe locale, etc.)
    - Calculs automatiques
    - Conformité réglementaire
    - Rapports fiscaux

## 🏗️ Architecture

### 🎯 Architecture Générale

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │    Business     │    │      Data       │
│      Layer      │◄──►│     Layer       │◄──►│     Layer       │
│                 │    │                 │    │                 │
│ • Controllers   │    │ • Services      │    │ • Repositories  │
│ • DTOs          │    │ • Business      │    │ • Entities      │
│ • Validation    │    │   Logic         │    │ • Cassandra     │
│ • Mapping       │    │ • Workflows     │    │ • Redis Cache   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 🔗 Architecture Microservices Ready

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │    │  Load Balancer  │    │   Discovery     │
│                 │    │                 │    │    Service      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
    ┌────────────────────────────┼────────────────────────────┐
    │                            │                            │
┌───▼────┐  ┌─────────────┐  ┌──▼──────┐  ┌─────────────┐  ┌─────────────┐
│Billing │  │  Payment    │  │Analytics│  │Notification │  │   Document  │
│Service │  │   Service   │  │Service  │  │   Service   │  │   Service   │
└────────┘  └─────────────┘  └─────────┘  └─────────────┘  └─────────────┘
```

### 🗃️ Modèle de Données

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Client    │    │   Facture   │    │   Produit   │
│             │◄──►│             │◄──►│             │
│ • ID        │    │ • Numéro    │    │ • Code      │
│ • Nom       │    │ • Date      │    │ • Prix      │
│ • Email     │    │ • Montant   │    │ • TVA       │
│ • Adresse   │    │ • Statut    │    │ • Stock     │
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       │                   │                   │
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Paiement   │    │LigneFacture │    │    Taxes    │
│             │    │             │    │             │
│ • Montant   │    │ • Quantité  │    │ • Taux      │
│ • Date      │    │ • Prix Unit │    │ • Type      │
│ • Mode      │    │ • Remise    │    │ • Base      │
│ • Statut    │    │ • Total     │    │ • Montant   │
└─────────────┘    └─────────────┘    └─────────────┘
```

## 💻 Technologies

### 🏗️ Backend Core
- **Java 21** - Langage de programmation principal
- **Spring Boot 3.5.5** - Framework principal
- **Spring Data Cassandra** - Accès aux données NoSQL
- **Spring Security** - Sécurité et authentification
- **Spring Cache** - Gestion du cache
- **MapStruct** - Mapping entre entités et DTOs

### 🗄️ Base de Données
- **Apache Cassandra** - Base de données NoSQL principale
- **Redis** - Cache en mémoire et sessions
- **Scylla DB** - Alternative performante à Cassandra (optionnel)

### 🌐 Web & API
- **Spring Web MVC** - API REST
- **OpenAPI 3 / Swagger** - Documentation API
- **Thymeleaf** - Templates HTML pour emails/PDF
- **JWT** - Authentification stateless

### 📄 Documents & Communication
- **OpenHTMLtoPDF** - Génération de PDF
- **Spring Mail** - Envoi d'emails
- **MailHog** - Tests d'emails en développement

### 🔧 Outils & Utilitaires
- **Lombok** - Réduction du boilerplate
- **Apache Commons** - Utilitaires Java
- **Maven** - Gestion des dépendances
- **Docker** - Containerisation

### 📊 Monitoring & DevOps
- **Spring Actuator** - Métriques et monitoring
- **Prometheus** - Collecte de métriques
- **SLF4J + Logback** - Système de logs

## ⚙️ Installation

### 📋 Prérequis

- **Java 21+** (OpenJDK ou Oracle)
- **Maven 3.8+**
- **Docker & Docker Compose** (recommandé)
- **Apache Cassandra 4.0+** ou **Scylla DB**
- **Redis 6.0+**

### 🚀 Installation Rapide avec Docker

```bash
# 1. Cloner le repository
git clone https://github.com/yooyob/erp-facturation.git
cd erp-facturation

# 2. Lancer l'environnement avec Docker Compose
docker-compose up -d

# 3. Construire et lancer l'application
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

### 🔧 Installation Manuelle

```bash
# 1. Installation Cassandra
# Ubuntu/Debian
sudo apt update
sudo apt install cassandra

# macOS avec Homebrew
brew install cassandra

# 2. Installation Redis
# Ubuntu/Debian
sudo apt install redis-server

# macOS avec Homebrew
brew install redis

# 3. Démarrage des services
sudo systemctl start cassandra
sudo systemctl start redis

# 4. Création du keyspace Cassandra
cqlsh -e "CREATE KEYSPACE IF NOT EXISTS facturation_erp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};"

# 5. Compilation et lancement
mvn clean install
mvn spring-boot:run
```

## 🔧 Configuration

### 📁 Structure de Configuration

```
src/main/resources/
├── application.yml          # Configuration principale
├── application-dev.yml      # Configuration développement
├── application-prod.yml     # Configuration production
├── application-docker.yml   # Configuration Docker
└── static/
    └── images/
        └── logo.png         # Logo pour les PDF
```

### ⚙️ Variables d'Environnement

```bash
# Base de données
CASSANDRA_HOST=localhost
CASSANDRA_PORT=9042
CASSANDRA_KEYSPACE=facturation_erp

# Cache Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=yourpassword

# Sécurité
JWT_SECRET=your-super-secret-jwt-key-here
JWT_EXPIRATION=86400000

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@domain.com
MAIL_PASSWORD=your-app-password

# Application
APP_BASE_URL=https://your-domain.com
LOG_LEVEL=INFO
```

### 🏗️ Configuration Cassandra

```yaml
spring:
  cassandra:
    contact-points: ${CASSANDRA_HOST:127.0.0.1}
    port: ${CASSANDRA_PORT:9042}
    local-datacenter: datacenter1
    keyspace-name: ${CASSANDRA_KEYSPACE:facturation_erp}
    schema-action: create_if_not_exists
    request:
      timeout: 10s
    connection:
      connect-timeout: 10s
```

### 📧 Configuration Email

```yaml
spring:
  mail:
    host: ${MAIL_HOST:localhost}
    port: ${MAIL_PORT:1025}
    username: ${MAIL_USERNAME:}
    password: ${MAIL_PASSWORD:}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

## 📚 API Documentation

### 🌐 Accès à la Documentation

Une fois l'application lancée, la documentation interactive est disponible à :

- **Swagger UI** : http://localhost:8080/docs
- **OpenAPI JSON** : http://localhost:8080/v3/api-docs

### 🔗 Endpoints Principaux

#### 👥 Gestion des Clients
```http
GET     /api/clients              # Liste des clients
POST    /api/clients              # Création d'un client
GET     /api/clients/{id}         # Détails d'un client
PUT     /api/clients/{id}         # Modification d'un client
DELETE  /api/clients/{id}         # Suppression d'un client
```

#### 📄 Gestion des Factures
```http
GET     /api/factures             # Liste des factures
POST    /api/factures             # Création d'une facture
GET     /api/factures/{id}        # Détails d'une facture
PUT     /api/factures/{id}        # Modification d'une facture
DELETE  /api/factures/{id}        # Suppression d'une facture
POST    /api/factures/{id}/pdf    # Génération PDF
POST    /api/factures/{id}/email  # Envoi par email
```

#### 💰 Gestion des Paiements
```http
GET     /api/paiements            # Liste des paiements
POST    /api/paiements            # Enregistrement paiement
GET     /api/paiements/{id}       # Détails d'un paiement
PUT     /api/paiements/{id}       # Modification paiement
```

#### 📊 Analytics et Statistiques
```http
GET     /api/analytics/dashboard  # Dashboard principal
GET     /api/analytics/ca         # Chiffre d'affaires
GET     /api/analytics/clients    # Performance clients
GET     /api/analytics/produits   # Performance produits
```

### 📖 Exemples d'Utilisation

#### Création d'une Facture
```bash
curl -X POST http://localhost:8080/api/factures \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "clientId": "123e4567-e89b-12d3-a456-426614174000",
    "dateFacture": "2024-01-15",
    "dateEcheance": "2024-02-15",
    "lignes": [
      {
        "produitId": "123e4567-e89b-12d3-a456-426614174001",
        "quantite": 2,
        "prixUnitaire": 100.00,
        "tauxTva": 20.0
      }
    ]
  }'
```

#### Récupération du Dashboard
```bash
curl -X GET http://localhost:8080/api/analytics/dashboard \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -G -d "dateDebut=2024-01-01" -d "dateFin=2024-12-31"
```

## 🧪 Tests

### 🎯 Stratégie de Tests

- **Tests Unitaires** : Couverture > 80%
- **Tests d'Intégration** : API et base de données
- **Tests de Performance** : Charge et stress
- **Tests de Sécurité** : Vulnérabilités et authentification

### 🏃‍♂️ Exécution des Tests

```bash
# Tests unitaires uniquement
mvn test

# Tests d'intégration
mvn integration-test

# Tests avec couverture
mvn test jacoco:report

# Tests de performance
mvn test -Dtest=PerformanceTest

# Tous les tests
mvn verify
```

### 📊 Rapports de Tests

Les rapports sont générés dans `target/site/jacoco/` et incluent :
- Couverture de code
- Rapports de tests
- Métriques de performance

## 📦 Déploiement

### 🐳 Déploiement Docker

```bash
# 1. Construction de l'image
docker build -t erp-facturation:latest .

# 2. Lancement avec Docker Compose
docker-compose -f docker-compose.prod.yml up -d

# 3. Vérification du déploiement
docker-compose ps
docker-compose logs -f app
```

### ☁️ Déploiement Cloud

#### AWS ECS/Fargate
```bash
# 1. Build et push vers ECR
aws ecr get-login-password --region eu-west-1 | docker login --username AWS --password-stdin xxxxx.dkr.ecr.eu-west-1.amazonaws.com
docker tag erp-facturation:latest xxxxx.dkr.ecr.eu-west-1.amazonaws.com/erp-facturation:latest
docker push xxxxx.dkr.ecr.eu-west-1.amazonaws.com/erp-facturation:latest

# 2. Déploiement ECS
aws ecs update-service --cluster erp-cluster --service erp-facturation-service --force-new-deployment
```

#### Kubernetes
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: erp-facturation
spec:
  replicas: 3
  selector:
    matchLabels:
      app: erp-facturation
  template:
    metadata:
      labels:
        app: erp-facturation
    spec:
      containers:
      - name: app
        image: erp-facturation:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
```

### 🔧 Configuration Production

```yaml
# application-prod.yml
spring:
  cassandra:
    contact-points: ${CASSANDRA_CLUSTER_HOSTS}
    ssl: true
    auth-provider:
      class: PlainTextAuthProvider
      username: ${CASSANDRA_USERNAME}
      password: ${CASSANDRA_PASSWORD}

  data:
    redis:
      cluster:
        nodes: ${REDIS_CLUSTER_NODES}
      ssl: true
      password: ${REDIS_PASSWORD}

logging:
  level:
    com.yooyob.erp: INFO
    root: WARN
  appender:
    console:
      enabled: false
    file:
      enabled: true
```

## 🔒 Sécurité

### 🛡️ Authentification et Autorisation

- **JWT Tokens** : Authentification stateless
- **Spring Security** : Contrôle d'accès granulaire
- **RBAC** : Rôles et permissions (Admin, User, ReadOnly)
- **OAuth2** : Integration possible avec des providers externes

### 🔐 Sécurisation des Données

- **Chiffrement** : Données sensibles chiffrées en base
- **HTTPS** : Communication sécurisée
- **Input Validation** : Validation stricte des entrées
- **SQL Injection** : Protection native avec Spring Data

### 🚨 Monitoring de Sécurité

- **Audit Trail** : Traçabilité des actions
- **Rate Limiting** : Protection contre les attaques DDoS
- **Security Headers** : Headers de sécurité automatiques
- **Vulnerability Scanning** : Scans réguliers des dépendances

### 🔑 Configuration Sécurisée

```yaml
# Exemple de configuration sécurisée
spring:
  security:
    jwt:
      secret: ${JWT_SECRET}
      expiration: 86400000
    headers:
      frame-options: DENY
      content-type-options: nosniff
      x-xss-protection: "1; mode=block"
```

## 📊 Monitoring

### 📈 Métriques Applicatives

- **Spring Actuator** : Health checks et métriques
- **Prometheus** : Collecte de métriques
- **Grafana** : Dashboards de monitoring
- **Custom Metrics** : Métriques métier spécifiques

### 🚨 Alerting

- **Alertmanager** : Gestion des alertes
- **Email/Slack** : Notifications multi-canaux
- **Thresholds** : Seuils configurables

### 📊 Dashboards Disponibles

1. **Dashboard Infrastructure**
    - CPU, Mémoire, Disque
    - Latence réseau
    - Statut des services

2. **Dashboard Application**
    - Requêtes/seconde
    - Temps de réponse
    - Taux d'erreur
    - Throughput

3. **Dashboard Métier**
    - Factures créées/jour
    - Chiffre d'affaires temps réel
    - Taux de paiement
    - Performance par client

### 🔍 Logs et Debugging

```yaml
# Configuration des logs
logging:
  level:
    com.yooyob.erp: DEBUG
    org.springframework.data.cassandra: WARN
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/facturation-erp.log
    max-size: 100MB
    max-history: 30
```

## 🤝 Contribution

### 📝 Guide de Contribution

1. **Fork** le repository
2. **Créer** une branche feature (`git checkout -b feature/AmazingFeature`)
3. **Commiter** les changements (`git commit -m 'Add some AmazingFeature'`)
4. **Pusher** vers la branche (`git push origin feature/AmazingFeature`)
5. **Ouvrir** une Pull Request

### 🎯 Standards de Code

- **Conventions Java** : Respect des conventions Oracle
- **Lombok** : Utilisation pour réduire le boilerplate
- **JavaDoc** : Documentation des méthodes publiques
- **Tests** : Couverture minimale de 80%

### 🔍 Code Review

- **Revue obligatoire** : Toute PR doit être reviewée
- **CI/CD** : Tests automatiques sur chaque PR
- **Quality Gates** : SonarQube et autres outils de qualité

### 📋 Issues et Bugs

- **Template** : Utiliser les templates GitHub
- **Labels** : Catégorisation appropriée
- **Priorité** : Critical, High, Medium, Low



### 📄 License

Ce projet est sous licence privée. Voir le fichier `LICENSE` pour plus de détails.

---


Pour toute question ou suggestion, n'hésitez pas à me contacter ou à ouvrir une issue sur GitHub.