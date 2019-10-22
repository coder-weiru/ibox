import 'package:flutter/material.dart';
import 'package:iplanner_ui/model/event_list.dart';
import 'package:optional/optional.dart';
import 'package:provider/provider.dart';

class EventPageState extends State<EventPage> {
  final TextStyle _biggerFont = const TextStyle(fontSize: 18.0);

  Widget _buildRow(Event event) {
    return ListTile(
      title: Text(
        event.summary,
        style: _biggerFont,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final eventList = Provider.of<EventList>(context);

    return ListView.builder(
        padding: const EdgeInsets.all(16.0),
        itemBuilder: (context, i) {
          if (i.isOdd) return Divider();

          final index = i ~/ 2;
          final event = eventList.getEventByPosition(index);
          if (Optional.ofNullable(event).isPresent) {
            return _buildRow(event);
          }
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

class EventPage extends StatefulWidget {
  @override
  EventPageState createState() => EventPageState();

  const EventPage();
}
