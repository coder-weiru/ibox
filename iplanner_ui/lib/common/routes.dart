import 'package:flutter/material.dart';
import 'package:iplanner_ui/screen/activities.dart';

import '../screen/activities.dart';
import '../screen/todos.dart';
import '../widget/backdrop_page.dart';
import '../widget/tabbed_page.dart';

const todoCalendarTab = MyTab(
    child: BackdropPage(
        child: TodoCalendarTab(),
        title: "Todo Calendar",
        description: "Your todo calendar",
        routeName: "todoCalendar"),
    tabName: "Todo Calendar",
    tabIndex: 0,
    navIcon: Icon(Icons.calendar_today),
    displayText: "Weekly");

const upcomingTodoTab = MyTab(
    child: BackdropPage(
        child: UpcomingTodoTab(),
        title: "Upcoming Todos",
        description: "Your upcoming todos",
        routeName: "upcomingTodo"),
    tabName: "Upcoming Todos",
    tabIndex: 1,
    navIcon: Icon(Icons.alarm),
    displayText: "Upcoming");

const todoSliderTab = MyTab(
    child: BackdropPage(
        child: TodoSliderTab(),
        title: "Todo Slider",
        description: "All Your Todos",
        routeName: "todoSlider"),
    tabName: "Todo Slider",
    tabIndex: 2,
    navIcon: Icon(Icons.forum),
    displayText: "Cards");

const todoRouteTabs = [todoCalendarTab, upcomingTodoTab, todoSliderTab];

const todoRoute = TabbedPage(
  tabs: todoRouteTabs,
  title: 'Todos',
  description: 'Your Todos',
  routeName: 'todo',
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
  Navigator.defaultRouteName: (context) => todoRoute,
  todoRoute.routeName: (context) => todoRoute,
  activityRoute.routeName: (context) => activityRoute
};
