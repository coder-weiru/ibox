import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../common/constants.dart' show MENU_EVENTS, MENU_ACTIVITIES, splash;
import '../common/settings.dart';

class BackgroundPanel extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return _getBackdropListTiles(context);
  }
}

ListView _getBackdropListTiles(BuildContext context) {
  return ListView(
    padding: EdgeInsets.only(bottom: 32.0),
    children: <Widget>[
      Card(
        semanticContainer: true,
        clipBehavior: Clip.antiAliasWithSaveLayer,
        child: splash,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10.0),
        ),
        elevation: 5,
        margin: EdgeInsets.all(5),
      ),
      Card(
          //                           <-- Card widget
          color: Colors.lightBlueAccent,
          elevation: 5,
          margin: EdgeInsets.all(5),
          child: ListTile(
            leading: Icon(Icons.event),
            title: Text(MENU_EVENTS),
            trailing: Icon(Icons.keyboard_arrow_right),
            onTap: () {
              Navigator.pushNamed(context, 'todo');
            },
          )),
      Card(
          //                           <-- Card widget
          color: Colors.lightBlueAccent,
          elevation: 5,
          margin: EdgeInsets.all(5),
          child: ListTile(
            leading: Icon(Icons.local_activity),
            title: Text(MENU_ACTIVITIES),
            trailing: Icon(Icons.keyboard_arrow_right),
            onTap: () {
              Navigator.pushNamed(context, 'activity');
            },
          )),
      Card(
          //                           <-- Card widget
          color: Colors.blueGrey,
          elevation: 5,
          margin: EdgeInsets.all(5),
          child: Consumer<MyAppSettings>(
              builder: (context, MyAppSettings settings, _) {
            return ListTile(
              onTap: () {},
              leading: Icon(settings.isDarkMode
                  ? Icons.brightness_4
                  : Icons.brightness_7),
              title: Text('Dark Mode'),
              trailing: Switch(
                onChanged: (bool value) => settings.setDarkMode(value),
                value: settings.isDarkMode,
              ),
            );
          })),
    ],
  );
}
