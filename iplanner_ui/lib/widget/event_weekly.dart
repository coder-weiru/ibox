import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:iplanner_ui/common/colors.dart';
import 'package:iplanner_ui/model/event_list.dart';

class EventWeekly extends StatelessWidget {
  final Map<String, List<Event>> _eventMap;

  const EventWeekly({@required eventMap}) : _eventMap = eventMap;

  Map<String, List<Event>> get events => this._eventMap;

  final List<String> _weekDays = const [
    'Sun',
    'Mon',
    'Tue',
    'Wed',
    'Thu',
    'Fri',
    'Sat'
  ];

  Widget _buildRow(BuildContext context, int index) {
    final activity = _eventMap.keys.elementAt(index);
    final eventList = this._eventMap[activity];
    return Container(
        height: 60.0,
        decoration: BoxDecoration(
          color: Color.fromRGBO(223, 230, 233, 0.5),
        ),
        child: GridView.builder(
            gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: _weekDays.length,
            ),
            itemCount: _weekDays.length,
            itemBuilder: (context, index) {
              final event = getEventByWeekDay(eventList, index);
              return Container(
                  decoration: BoxDecoration(
                    color: Color.fromRGBO(223, 230, 233, 0.5),
                    border:
                        Border.all(color: Colors.lightBlueAccent, width: 0.5),
                  ),
                  child: Visibility(
                      visible: event != null,
                      child: Center(
                          child: new Card(
                              color: getEventColor(activity),
                              elevation: 5.0,
                              child: new Container(
                                  alignment: Alignment.center,
                                  margin: new EdgeInsets.only(
                                      top: 2.0,
                                      bottom: 2.0,
                                      left: 2.0,
                                      right: 2.0),
                                  child: Tooltip(
                                      message: event != null
                                          ? new DateFormat("EEEE, MMMM d")
                                              .format(event.start)
                                          : "",
                                      child: new Text(
                                          event != null ? event.summary : "",
                                          style: TextStyle(
                                            color: Colors.black45,
                                            fontSize: 10.0,
                                            fontWeight: FontWeight.bold,
                                          ))))))));
            }));
  }

  @override
  Widget build(BuildContext context) {
    return Container(
        padding: EdgeInsets.all(5.0),
        child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              Container(
                  padding: EdgeInsets.all(5.0),
                  height: 60.0,
                  decoration: BoxDecoration(
                    color: Color.fromRGBO(223, 230, 233, 0.5),
                    borderRadius: BorderRadius.circular(25.0),
                  ),
                  child: GridView.builder(
                      gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                        crossAxisCount: _weekDays.length,
                      ),
                      itemCount: _weekDays.length,
                      itemBuilder: (context, index) {
                        return Center(
                            child: new Card(
                                color: Colors.amberAccent,
                                elevation: 5.0,
                                child: new Container(
                                    alignment: Alignment.center,
                                    margin: new EdgeInsets.only(
                                        top: 5.0,
                                        bottom: 5.0,
                                        left: 5.0,
                                        right: 5.0),
                                    decoration: new BoxDecoration(
                                      color: Colors.white54,
                                      shape: BoxShape.circle,
                                    ),
                                    child: new Text(
                                      _weekDays[index],
                                      style: TextStyle(
                                        color: Colors.redAccent,
                                        fontSize: 18.0,
                                        fontWeight: FontWeight.bold,
                                      ),
                                    ))));
                      })),
              Expanded(
                child: // Events by activities
                    Container(
                        decoration: BoxDecoration(
                          color: Color.fromRGBO(223, 230, 233, 0.5),
                          borderRadius: BorderRadius.circular(25.0),
                        ),
                        child: new ListView.builder(
                            itemCount: _eventMap.keys.toList().length,
                            itemBuilder: (BuildContext context, int index) {
                              return _buildRow(context, index);
                            })),
              )
            ]));
  }
}

Event getEventByWeekDay(List<Event> events, int weekdayIndex) {
  return events.firstWhere((event) {
    final start = event.start;
    return start.weekday == weekdayIndex;
  }, orElse: () => null);
}
