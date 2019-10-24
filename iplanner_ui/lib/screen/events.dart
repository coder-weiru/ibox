import 'package:carousel_slider/carousel_slider.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:iplanner_ui/model/event_list.dart';
import 'package:provider/provider.dart';

import '../common/colors.dart';
import '../common/constants.dart';

class UpcomingEventTabState extends State<UpcomingEventTab> {
  Widget _buildRow(Event event) {
    if (event != null) {
      final activity = event.activity;
      final summary = event.summary;
      final start =
          new DateFormat("EEEE, MMMM d, 'at' HH:mm").format(event.start);
      return Card(
          //                           <-- Card widget
          color: getEventColor(activity),
          elevation: 5,
          child: ListTile(
            leading: Icon(Icons.access_alarm),
            title: Text(
              summary,
              style: TEXT_TITLE_FONT,
            ),
            subtitle: Text(
              start,
              style: TEXT_SUBTITLE_FONT,
            ),
            trailing: Icon(Icons.expand_more),
            onTap: () {},
          ));
    } else {
      return null;
    }
  }

  @override
  Widget build(BuildContext context) {
    final eventList = Provider.of<EventList>(context);

    return ListView.builder(
        padding: const EdgeInsets.all(5.0),
        itemBuilder: (context, idx) {
          final index = idx ~/ 2;
          final event = eventList.getEventByPosition(index);
          return _buildRow(event);
        });
  }
}

class UpcomingEventTab extends StatefulWidget {
  @override
  UpcomingEventTabState createState() => UpcomingEventTabState();

  const UpcomingEventTab();
}

class EventCalendarTab extends StatelessWidget {
  const EventCalendarTab();

  @override
  Widget build(BuildContext context) {
    return Icon(Icons.cloud, size: 64.0, color: Colors.teal);
  }
}

class EventSliderTabState extends State<EventSliderTab> {
  //
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
    final eventListModel = Provider.of<EventList>(context);
    final eventList = eventListModel.getAllEvents();
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
            items: eventList.map((event) {
              final int index = random.nextInt(17);
              final image = imageList[index];
              return Builder(
                builder: (BuildContext context) {
                  return Container(
                    width: MediaQuery.of(context).size.width,
                    margin: EdgeInsets.symmetric(horizontal: 5.0),
                    decoration: BoxDecoration(
                      color: Colors.green,
                    ),
                    child: image,
                  );
                },
              );
            }).toList(),
          ),
          SizedBox(
            height: 10,
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: map<Widget>(eventList, (index, url) {
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

class EventSliderTab extends StatefulWidget {
  @override
  EventSliderTabState createState() => EventSliderTabState();

  const EventSliderTab();
}
