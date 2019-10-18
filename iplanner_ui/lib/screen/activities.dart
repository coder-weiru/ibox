import 'package:flutter/material.dart';
import 'package:iplanner_ui/model/activity_list.dart';
import 'package:optional/optional.dart';
import 'package:provider/provider.dart';

class ActivityPageState extends State<ActivityPage> {
  final TextStyle _biggerFont = const TextStyle(fontSize: 18.0);

  Widget _buildRow(Activity activity) {
    return ListTile(
      title: Text(
        activity.title,
        style: _biggerFont,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final activityList = Provider.of<ActivityList>(context);

    return ListView.builder(
        padding: const EdgeInsets.all(16.0),
        itemBuilder: (context, i) {
          if (i.isOdd) return Divider();

          final index = i ~/ 2;
          final activity = activityList.getActivityByPosition(index);
          if (Optional.ofNullable(activity).isPresent) {
            return _buildRow(activity);
          }
        });
  }

  void _pushSaved() {
    Navigator.of(context).push(
      MaterialPageRoute<void>(
        builder: (BuildContext context) {
          final activityList = Provider.of<ActivityList>(context);
          final _saved = activityList.getSavedActivity();
          final Iterable<ListTile> tiles = _saved.map(
            (Activity activity) {
              return ListTile(
                title: Text(
                  activity.title,
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
              title: Text('Saved Suggestions'),
            ),
            body: ListView(children: divided),
          );
        },
      ),
    );
  }
}

class ActivityPage extends StatefulWidget {
  @override
  ActivityPageState createState() => ActivityPageState();

  const ActivityPage();
}
