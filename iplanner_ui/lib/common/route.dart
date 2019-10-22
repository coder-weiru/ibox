import 'package:backdrop/backdrop.dart';
import 'package:flutter/material.dart';

import '../widget/background_panel.dart';
import 'constants.dart' show HEADER_HEIGHT;

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

  String get routeName => this._routeName;

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
          SizedBox(height: backLayerHeight, child: BackgroundPanel())
        ],
      ),
    );
  }

  List<Widget> _getAppbarActions(BuildContext context) {
    return <Widget>[
      //
    ];
  }
}
