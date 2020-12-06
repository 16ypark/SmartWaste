# SmartWaste
CS489 Project: Let users find nearby waste bins so that less waste is thrown to the streets!

## [ About ]
This is a term project of Team 3 for Computer Ethics and Social Issues class. We aim to promote ethical decisions with regards to throwing trash away by showing the users where nearby trash cans are. 

### Details
We developed an Android application which shows the users where nearby trash cans are. The location of trash cans are shown with a marker, and depending on the category of the trash cans(normal, recycle or large) the markers are shown with different image icons. Users are also able to add trash cans to the database if they find one that is not registered to the database. In order to ensure integrity of the data, users are also able to report trash cans that were added with false information. Apart from the data obtained from the collective effort of the users, we have also manually registered data of trash cans from public resources for initial robustness of data. Because there are many markers shown in the map, they were clustered using TedNaverMapClustering library.

## [ Preview ]
| 스크린샷1                    | 스크린샷2                                  | 사용예시                                  |
|:------------------------------:|:---------------------------------:|:---------------------------------:|
|![](ReadMeResource/screenshot_1.png) |![](ReadMeResource/screenshot_2.png) |![](ReadMeResource/simulation.gif) |

## [ Develop Environment ]
- Android Studio version : `4.0.2`

## [ API ]
- [NAVER Maps](https://navermaps.github.io/android-map-sdk/)
- [Firebase Realtime Database](https://github.com/firebase/firebase-android-sdk)

## [ Library ]
- [TedNaverMapClustering](https://github.com/ParkSangGwon/TedNaverMapClustering)
- [Retrofit](https://github.com/square/retrofit)
- [Gson](https://github.com/google/gson)
