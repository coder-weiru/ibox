import 'package:flutter/material.dart';
import 'package:backdrop/backdrop.dart';
import 'package:provider/provider.dart';
import 'package:url_launcher/url_launcher.dart' as url_launcher;

import 'constants.dart' show APP_DESCRIPTION, APP_NAME, APP_VERSION, HEADER_HEIGHT;
import 'settings.dart';
import 'constants.dart';

class MyRoute extends StatelessWidget {
  // Actual content of the example.
  final Widget child;
  // Title shown in the route's appbar. By default just returns routeName.
  final String _title;
  // A short description of the route. If not null, will be shown as subtitle in
  final String description;
  // The name of the route.
  final String _routeName;

  const MyRoute({
    Key key,
    @required this.child,
    String title,
    this.description,
    @required routeName,
  })  : _title = title,
        _routeName = routeName,
        super(key: key);

  String get routeName =>
      this._routeName;

  String get title => _title ?? this.routeName;

  @override
  Widget build(BuildContext context) {
    final double headerHeight = HEADER_HEIGHT;
    final double appbarHeight = kToolbarHeight;
    final double backLayerHeight =
        MediaQuery.of(context).size.height - headerHeight - appbarHeight;
    return BackdropScaffold(
      title: SingleChildScrollView(
        scrollDirection: Axis.horizontal,
        child: Text(this.title),
      ),
      actions: _getAppbarActions(context),
      iconPosition: BackdropIconPosition.action,
      headerHeight: headerHeight,
      frontLayer: this.child,
      // To make the listview in backlayer scrollable, had to calculate the
      // height of backlayer, and wrap inside a Column. This is due to the
      // implementation of BackdropScaffold ('backdrop' package, v0.1.8).
      backLayer: Column(
        children: <Widget>[
          SizedBox(height: backLayerHeight, child: _getBackdropListTiles())
        ],
      ),
    );
  }

  List<Widget> _getAppbarActions(BuildContext context) {
    return <Widget>[
      //RandomWords()
    ];
  }

  ListView _getBackdropListTiles() {
    return ListView(
      padding: EdgeInsets.only(bottom: 32.0),
      children: <Widget>[
        ListTile(
          leading: splashSvg,
          title: Text(APP_NAME),
          subtitle: Text(APP_VERSION),
        ),
        Consumer<MyAppSettings>(builder: (context, MyAppSettings settings, _) {
          return ListTile(
            onTap: () {},
            leading: Icon(
                settings.isDarkMode ? Icons.brightness_4 : Icons.brightness_7),
            title: Text('Dark mode'),
            trailing: Switch(
              onChanged: (bool value) => settings.setDarkMode(value),
              value: settings.isDarkMode,
            ),
          );
        }),
      ],
    );
  }
}