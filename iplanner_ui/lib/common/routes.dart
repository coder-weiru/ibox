import 'package:flutter/material.dart';
import 'package:iplanner_ui/screen/activities.dart';

import '../screen/activities.dart';
import '../screen/events.dart';
import 'backdrop_page.dart';
import 'tabbed_page.dart';

const eventCalendarTab = MyTab(
    child: BackdropPage(
        child: EventCalendarTab(),
        title: "Event Calendar",
        description: "Your event calendar",
        routeName: "eventCalendar"),
    tabName: "Event Calendar",
    tabIndex: 0,
    navIcon: Icon(Icons.calendar_today),
    displayText: "Weekly");

const upcomingEventTab = MyTab(
    child: BackdropPage(
        child: UpcomingEventTab(),
        title: "Upcoming Events",
        description: "Your upcoming events",
        routeName: "upcomingEvent"),
    tabName: "Upcoming Events",
    tabIndex: 1,
    navIcon: Icon(Icons.alarm),
    displayText: "Upcoming");

const eventSliderTab = MyTab(
    child: BackdropPage(
        child: EventSliderTab(),
        title: "Event Slider",
        description: "All Your Events",
        routeName: "eventSlider"),
    tabName: "Event Slider",
    tabIndex: 2,
    navIcon: Icon(Icons.forum),
    displayText: "Cards");

const eventRouteTabs = [eventCalendarTab, upcomingEventTab, eventSliderTab];

const eventRoute = TabbedPage(
  tabs: eventRouteTabs,
  title: 'Events',
  description: 'Your Events',
  routeName: 'event',
);

const activityListTab = MyTab(
    child: BackdropPage(
        child: ActivityListTab(),
        title: "Activity List",
        description: "Your Activity List",
        routeName: "activityList"),
    tabName: "Activity List",
    tabIndex: 0,
    navIcon: Icon(Icons.local_activity),
    displayText: "List");

const activitySliderTab = MyTab(
    child: BackdropPage(
        child: ActivitySliderTab(),
        title: "Activity Slider",
        description: "All Your Activities",
        routeName: "activitySlider"),
    tabName: "Activity Slider",
    tabIndex: 1,
    navIcon: Icon(Icons.forum),
    displayText: "Cards");

const activityRouteTabs = [activityListTab, activitySliderTab];

const activityRoute = TabbedPage(
  tabs: activityRouteTabs,
  title: 'Activities',
  description: 'Define the common activities',
  routeName: 'activity',
);

final Map<String, WidgetBuilder> appRoutingTable = {
  Navigator.defaultRouteName: (context) => eventRoute,
  eventRoute.routeName: (context) => eventRoute,
  activityRoute.routeName: (context) => activityRoute
};
