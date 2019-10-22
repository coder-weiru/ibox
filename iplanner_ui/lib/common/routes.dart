import 'package:flutter/material.dart';
import 'package:iplanner_ui/screen/activities.dart';

import '../screen/activities.dart';
import '../screen/events.dart';
import 'route.dart';

const eventPage = EventPage();
const activityPage = ActivityPage();

const eventRoute = MyRoute(
  child: eventPage,
  title: 'Events',
  description: 'Your Upcoming events',
  routeName: 'event',
);

const activityRoute = MyRoute(
  child: activityPage,
  title: 'Activities',
  description: 'Define the common activities',
  routeName: 'activity',
);

final Map<String, WidgetBuilder> appRoutingTable = {
  Navigator.defaultRouteName: (context) => eventRoute,
  eventRoute.routeName: (context) => eventRoute,
  activityRoute.routeName: (context) => activityRoute
};
