[![Build Status](https://travis-ci.org/adw1n/hub-traffic.svg?branch=master)](https://travis-ci.org/adw1n/hub-traffic)

WORK IN PROGRESS

<https://hubtraffic.adw1n.com/>

### Motivation
Under the `Insights->Graphs->Traffic` on your repo page, GitHub offers charts that present the traffic statistics (views/clones numbers) for your repo. Unfortunately only stats from the last 14 days are accessible. 

### !WARNING!  
Whoever hosts this app **has access to all of the users' GitHub API tokens** and repo traffic stats. Access to the tokens is needed for the cron job that updates users' repo stats daily (so that you can log in just once a year, and have stats accessible from the whole year period). Because of that **I recommend self-hosting this app for yourself**.

Users can revoke their OAuth tokens granted to the hub-traffic app at any time from the GitHub page <https://github.com/settings/applications>.

### Deployment
1. register new GitHub OAuth app on <https://github.com/settings/applications/new>  
relevant docs: <https://developer.github.com/apps/building-integrations/setting-up-and-registering-oauth-apps/registering-oauth-apps/>
2.
```bash
git clone https://github.com/adw1n/hub-traffic
cd hub-traffic
# put in src/main/resources/application.yml your Client ID and Client Secret obtained during step 1)
sudo mkdir -p /opt/hub-traffic/pgdata
sudo docker-compose build
sudo docker-compose up -d
```
App should be now accessible at <http://localhost:8080>.

I created a sample nginx config file [config/hubtraffic.example.com.nginx](config/hubtraffic.example.com.nginx) that you can put in `/etc/nginx/sites-available` and then symlink to if from `/etc/nginx/sites-enabled`. Obviously you need to change the domain name both in the file name and in the file itself.
```bash
sudo cp config/hubtraffic.example.com.nginx /etc/nginx/sites-available
sudo ln -s /etc/nginx/sites-available/hubtraffic.example.com.nginx  /etc/nginx/sites-enabled/hubtraffic.example.com
sudo service nginx reload
```
