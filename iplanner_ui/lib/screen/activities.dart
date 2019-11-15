import 'package:carousel_slider/carousel_slider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../model/activity_list.dart';
import '../model/attributes.dart';
import '../widget/activity_card.dart';

class ActivityListTabState extends State<ActivityListTab> {
  Color getActivityColor(Activity activity) {
    Color color = Colors.white;
    TagAttribute tagAttribute = activity.tags;
    if (tagAttribute != null) {
      Tag primaryTag = tagAttribute.getPrimaryTag();
      if (primaryTag != null && primaryTag.argbValue != null) {
        color = Color(int.parse(primaryTag.argbValue));
      }
    }
    return color;
  }

  Widget _buildRow(Activity activity) {
    if (activity != null) {
      final title = activity.title;
      final description = activity.description;
      return Card(
          color: getActivityColor(activity),
          elevation: 5,
          child: ListTile(
            //leading: _buildTags(activity.tags ?? TagAttribute()),
            title: Text(
              title,
              style: Theme.of(context).textTheme.title,
            ),
            subtitle: Text(
              description,
              style: Theme.of(context).textTheme.subtitle,
            ),
            trailing: Icon(Icons.more_horiz),
            onTap: () {},
          ));
    } else {
      return null;
    }
  }

  @override
  Widget build(BuildContext context) {
    final activityList = Provider.of<ActivityList>(context);
    return ListView.builder(
        padding: const EdgeInsets.all(16.0),
        itemBuilder: (context, index) {
          final activity = activityList.getActivityByPosition(index);
          return _buildRow(activity);
        });
  }
}

class ActivityListTab extends StatefulWidget {
  @override
  ActivityListTabState createState() => ActivityListTabState();

  const ActivityListTab();
}

class ActivitySliderTabState extends State<ActivitySliderTab> {
  CarouselSlider _carouselSlider;
  int _current = 0;

  List<T> map<T>(List list, Function handler) {
    List<T> result = [];
    for (var i = 0; i < list.length; i++) {
      result.add(handler(i, list[i]));
    }
    return result;
  }

  @override
  Widget build(BuildContext context) {
    final activityListModel = Provider.of<ActivityList>(context);
    final activityList = activityListModel.getAllActivities();
    return Container(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: <Widget>[
          _carouselSlider = CarouselSlider(
            height: 550.0,
            initialPage: 0,
            enlargeCenterPage: true,
            autoPlay: true,
            reverse: false,
            enableInfiniteScroll: true,
            autoPlayInterval: Duration(seconds: 2),
            autoPlayAnimationDuration: Duration(milliseconds: 2000),
            pauseAutoPlayOnTouch: Duration(seconds: 10),
            scrollDirection: Axis.horizontal,
            onPageChanged: (index) {
              setState(() {
                _current = index;
              });
            },
            items: activityList.map((activity) {
              return Builder(
                builder: (BuildContext context) {
                  return Container(
                      width: MediaQuery.of(context).size.width,
                      margin: EdgeInsets.symmetric(horizontal: 5.0),
                      /*decoration: new BoxDecoration(
                        image: new DecorationImage(
                          image: image,
                          fit: BoxFit.cover,
                        ),
                      ),*/
                      child: new Stack(
                        children: <Widget>[
                          new Positioned(
                              left: 10.0,
                              right: 10.0,
                              bottom: 25.0,
                              child: ActivityCard(activity: activity)),
                        ],
                      ));
                },
              );
            }).toList(),
          ),
          SizedBox(
            height: 10,
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: map<Widget>(activityList, (index, url) {
              return Container(
                width: 10.0,
                height: 10.0,
                margin: EdgeInsets.symmetric(vertical: 10.0, horizontal: 2.0),
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: _current == index ? Colors.lightBlue : Colors.grey,
                ),
              );
            }),
          ),
          SizedBox(
            height: 10.0,
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              OutlineButton(
                onPressed: goToPrevious,
                child: Text("<"),
              ),
              OutlineButton(
                onPressed: goToNext,
                child: Text(">"),
              ),
            ],
          ),
        ],
      ),
    );
  }

  goToPrevious() {
    _carouselSlider.previousPage(
        duration: Duration(milliseconds: 300), curve: Curves.ease);
  }

  goToNext() {
    _carouselSlider.nextPage(
        duration: Duration(milliseconds: 300), curve: Curves.decelerate);
  }
}

class ActivitySliderTab extends StatefulWidget {
  @override
  ActivitySliderTabState createState() => ActivitySliderTabState();

  const ActivitySliderTab();
}
