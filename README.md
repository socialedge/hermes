# Hermes <img src="https://api.travis-ci.org/socialedge/hermes.svg?branch=develop"> <img src="https://www.versioneye.com/user/projects/57a9ca36c75d640045d205ec/badge.svg?style=flat"> <img src="https://img.shields.io/aur/license/yaourt.svg">
Hermes - The Municipal Transport Timetable System :)

## Deployment
### Heroku
```bash
## Clone repository
git clone https://github.com/socialedge/hermes.git
cd hermes/

## Add Heroku remotes (make sure you are logged in via $ heroku login)
heroku git:remote -a NAME_OF_YOUR_HEROKU_FRONTEND_APP -r heroku-frontend
heroku git:remote -a NAME_OF_YOUR_HEROKU_BACKEND_APP -r heroku-backend

## Push frontend application to Heroku
git subtree push --prefix frontend heroku-frontend master

## Push backend application to Heroku
git subtree push --prefix backend heroku-backend master
```

## Bugs and Feedback
For bugs, questions and discussions please use the [Github Issues](https://github.com/socialedge/hermes/issues).

## License
Except as otherwise noted this software is licensed under the [GNU General Public License, v3](http://www.gnu.org/licenses/gpl-3.0.txt)

Licensed under the GNU General Public License, v3 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.gnu.org/licenses/gpl-3.0.txt](http://www.gnu.org/licenses/gpl-3.0.txt)
