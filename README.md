# orderprocessor
demonstrates inheritance of JSON entities, ORM entities and spring-data repositories

# Quick start
```bash
#initialize database
sudo -u postgres psql
create database processor;
create user processor with password 'processor';
grant all privileges on database processor to processor;
\q

cd /path/to/project/
#start rabbit container
docker-compose up -d
#start app
mvn spring-boot:run
