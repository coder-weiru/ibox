import 'package:flutter/material.dart';
import 'package:iplanner_ui/screen/activities.dart';

import '../screen/activities.dart';
import 'route.dart';

const activityPage = ActivityPage();

const activityRoute = MyRoute(
  child: activityPage,
  title: 'Activities',
  description: 'Define the common activities',
  routeName: 'activity',
);

final Map<String, WidgetBuilder> appRoutingTable = {
  Navigator.defaultRouteName: (context) => activityRoute,
  activityRoute.routeName: (context) => activityRoute
};
