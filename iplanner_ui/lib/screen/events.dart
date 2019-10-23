import 'package:flutter/material.dart';
import 'package:iplanner_ui/model/event_list.dart';
import 'package:provider/provider.dart';

class UpcomingEventTabState extends State<UpcomingEventTab> {
  final TextStyle _biggerFont = const TextStyle(fontSize: 18.0);

  Widget _buildRow(Event event) {
    return ListTile(
      title: Text(
        event != null ? event.summary : "",
        style: _biggerFont,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final eventList = Provider.of<EventList>(context);

    return ListView.builder(
        padding: const EdgeInsets.all(16.0),
        itemBuilder: (context, idx) {
          if (idx.isOdd) return Divider();

          final index = idx ~/ 2;
          final event = eventList.getEventByPosition(index);
          return _buildRow(event);
        });
  }

  void _pushSaved() {
    Navigator.of(context).push(
      MaterialPageRoute<void>(
        builder: (BuildContext context) {
          final eventList = Provider.of<EventList>(context);
          final _saved = eventList.getSavedEvent();
          final Iterable<ListTile> tiles = _saved.map(
            (Event event) {
              return ListTile(
                title: Text(
                  event.summary,
                  style: _biggerFont,
                ),
              );
            },
          );
          final List<Widget> divided = ListTile.divideTiles(
            context: context,
            tiles: tiles,
          ).toList();

          return Scaffold(
            appBar: AppBar(
              title: Text('Saved Events'),
            ),
            body: ListView(children: divided),
          );
        },
      ),
    );
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

class EventSliderTab extends StatelessWidget {
  const EventSliderTab();

  @override
  Widget build(BuildContext context) {
    return Icon(Icons.forum, size: 64.0, color: Colors.teal);
  }
}
