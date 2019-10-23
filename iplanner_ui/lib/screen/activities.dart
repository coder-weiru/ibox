import 'package:flutter/material.dart';
import 'package:iplanner_ui/model/activity_list.dart';
import 'package:provider/provider.dart';

class ActivityListTabState extends State<ActivityListTab> {
  final TextStyle _biggerFont = const TextStyle(fontSize: 18.0);

  Widget _buildRow(Activity activity) {
    return ListTile(
      title: Text(
        activity != null ? activity.title : "",
        style: _biggerFont,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final activityList = Provider.of<ActivityList>(context);

    return ListView.builder(
        padding: const EdgeInsets.all(16.0),
        itemBuilder: (context, idx) {
          if (idx.isOdd) {
            return Divider();
          }
          final index = idx ~/ 2;
          final activity = activityList.getActivityByPosition(index);
          return _buildRow(activity);
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
              title: Text('Saved Activities'),
            ),
            body: ListView(children: divided),
          );
        },
      ),
    );
  }
}

class ActivityListTab extends StatefulWidget {
  @override
  ActivityListTabState createState() => ActivityListTabState();

  const ActivityListTab();
}

class ActivitySliderTab extends StatelessWidget {
  const ActivitySliderTab();

  @override
  Widget build(BuildContext context) {
    return Icon(Icons.forum, size: 64.0, color: Colors.teal);
  }
}
