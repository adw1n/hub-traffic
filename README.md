WORK IN PROGRESS

TODO link_to_the_app_here

### Motivation
Under the `Insights->Graphs->Traffic` on your repo page, GitHub offers charts that present the traffic statistics (views/clones numbers) for your repo. Unfortunately only stats from the last 14 days are accessible. 

### !WARNING!  
Whoever hosts this app **has access to all of the users' GitHub API tokens** and repo traffic stats. Access to the tokens is needed for the cron job that updates users' repo stats daily (so that you can log in just once a year, and have stats accessible from the whole year period).

Because of that **I recommend self-hosting this app for yourself**. For deployment instructions see the deployment section.

### Deployment
1. register new GitHub OAuth app on [https://github.com/settings/applications/new]()
relevant docs: [https://developer.github.com/apps/building-integrations/setting-up-and-registering-oauth-apps/registering-oauth-apps/]()
2.
```bash
git clone https://github.com/adw1n/hub-traffic
cd hub-traffic
# put in src/main/resources/application.yml your Client ID and Client Secret obtained during step 1)
sudo mkdir -p /opt/hub-traffic/pgdata
docker-compose build .
docker-compose up
```
