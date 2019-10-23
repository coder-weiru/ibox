import 'package:flutter/material.dart';

class SimplePage extends StatelessWidget {
  // Actual content of the example.
  final Widget child;
  // Title shown in the route's appbar. By default just returns routeName.
  final String _title;
  // A short description of the route. If not null, will be shown as subtitle in
  final String description;
  // The name of the route.
  final String _routeName;

  const SimplePage({
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
    return Scaffold(
      appBar: AppBar(
        title: Text(this.title),
      ),
      body: this.child,
    );
  }
}
