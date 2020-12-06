# SmartWaste
CS489 Project: Let users find nearby waste bins so that less waste is thrown to the streets!

# iOS_map_clustering
개발 기간 : 2020년 05월 11일  ~ 2020년 5월 22일

## [ About ]
This is a term project of Team 3 for Computer Ethics and Social Issues class. Its aim is to put practical knowledge obtained during the course to an actual use.
### Details
We developed an Android application which shows the users where nearby trash cans are. The location of trash cans are shown with a marker, and depending on the category of the trash cans(normal, recycle or large) the markers are shown with different image icons. Users are also able to add trash cans to the database if they find one that is not registered to the database. In order to ensure integrity of the data, users are also able to report trash cans that were added with false information. Apart from the data obtained from the collective effort of the users, we have also manually registered data of trash cans from public resources for initial robustness of data. Because there are many markers shown in the map, they were clustered using TedNaverMapClustering library.


## [ Preview ]


## [ Develop Environment ]
- Android Depolyment Target :


## [ Library ]
- [TedNaverMapClustering](https://github.com/ParkSangGwon/TedNaverMapClustering)
- [Retrofit](https://github.com/square/retrofit)
- [Gson](https://github.com/google/gson)
