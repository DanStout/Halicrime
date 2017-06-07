#HaliCrime
A webapp using crime data about Halifax, Nova Scotia

## DB Setup

```
CREATE USER crime_user WITH PASSWORD 'password';
CREATE DATABASE crime_db OWNER crime_user;
GRANT ALL PRIVILEGES ON DATABASE crime_db TO crime_user;
```

## To Do
* Investigate Kotlin DI
* Share ObjectMapper Instance
* Setup Flyway
* Fetch data once per week
* Create api to retrieve data
* Create VueJS webapp to view data


## Done
* Initial setup
* Load configuration from file
