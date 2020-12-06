# SmartWaste
CS489 Project: Let users find nearby waste bins so that less waste is thrown to the streets!

## [ About ]
This is a term project of Team 3 for Computer Ethics and Social Issues class. Its aim is to put practical knowledge obtained during the course to an actual use.
### Details
We developed an Android application which shows the users where nearby trash cans are. The location of trash cans are shown with a marker, and depending on the category of the trash cans(normal, recycle or large) the markers are shown with different image icons. Users are also able to add trash cans to the database if they find one that is not registered to the database. In order to ensure integrity of the data, users are also able to report trash cans that were added with false information. Apart from the data obtained from the collective effort of the users, we have also manually registered data of trash cans from public resources for initial robustness of data. Because there are many markers shown in the map, they were clustered using TedNaverMapClustering library.

## [ Preview ]


## [ Develop Environment ]
Android Studio 4.0.2
Build #AI-193.6911.18.40.6821437, built on September 9, 2020
Runtime version: 1.8.0_242-release-1644-b3-6222593 x86_64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o


## [ Library ]
- [TedNaverMapClustering](https://github.com/ParkSangGwon/TedNaverMapClustering)
- [Retrofit](https://github.com/square/retrofit)
- [Gson](https://github.com/google/gson)
