#HaliCrime
A webapp using crime data about Halifax, Nova Scotia

## Database

### Setup

```
ALTER USER postgres WITH PASSWORD 'long password';
CREATE USER crime_user WITH PASSWORD 'password';
CREATE DATABASE crime_db OWNER crime_user;
GRANT ALL PRIVILEGES ON DATABASE crime_db TO crime_user;
```

### Fix PostgreSQL's peer authentication:
* Edit `/etc/postgresql/9.x/main/pg_hba.conf` and change the line that says `local all all peer` to `local all all md5`
* Reload the configuration via `sudo service postgresql restart`


### Backup

```
pg_dump --username=crime_user --format=c --clean --create crime_db > crimes.dump


## Running

Backend: `gradlew run` will launch the server on the port configured in `config.json`

Frontend: `npm install` if first time; `npm run dev` to launch

## Building

Backend: Create an executable "fat jar" containing all dependencies by running `gradlew shadowjar`, which will create the jar in `/build/libs`

Frontend: `npm run build`

## Tasks
### To Do
* Investigate Kotlin DI
* Create VueJS webapp to view data
* Vert.x hosting static files
* Switch URLs in Vue app based on dev/prod


### Done
* Initial setup
* Load configuration from file
* Setup Flyway
* Add unique constraint to crimes table
* Reuse ObjectMapper Instance
* Fetch data once per week
* Create api to retrieve data