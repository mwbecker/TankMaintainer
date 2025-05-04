## Run locally

```source .env && mvn spring-boot:run```


## Docker Compose:


- To build and run docker container for backend:

```docker compose -f tankmaintainerbackend-docker-compose.yml up -d```


- To build and run docker container for mysql 

```docker compose -f tankmaintainer-mysql-docker-compose.yml up -d```


## Heroku

- Setup

```heroku login```

```heroku git:remote -a tankmaintainer-backend```

- Build and push

```git add .```

```git commit -m "Add changes"```

```git push heroku main```