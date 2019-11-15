import 'dart:math';

import 'package:carousel_slider/carousel_slider.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

import '../common/colors.dart';
import '../common/constants.dart';
import '../model/todo_list.dart';
import '../widget/todo_card.dart';
import '../widget/todo_weekly.dart';

class UpcomingTodoTabState extends State<UpcomingTodoTab> {
  Widget _buildRow(Todo todo) {
    if (todo != null) {
      final activity = todo.activity;
      final summary = todo.summary;
      final start =
          new DateFormat("EEEE, MMMM d, 'at' HH:mm").format(todo.start);
      return Card(
          color: getTodoColor(activity),
          elevation: 5,
          child: ListTile(
            leading: Icon(Icons.access_alarm),
            title: Text(
              summary,
              style: Theme.of(context).textTheme.title,
            ),
            subtitle: Text(
              start,
              style: Theme.of(context).textTheme.subtitle,
            ),
            trailing: Icon(Icons.expand_more),
            onTap: () {},
          ));
    } else {
      return null;
    }
  }

  @override
  Widget build(BuildContext context) {
    final todoList = Provider.of<TodoList>(context);

    return ListView.builder(
        padding: const EdgeInsets.all(5.0),
        itemBuilder: (context, idx) {
          final index = idx ~/ 2;
          final todo = todoList.getTodoByPosition(index);
          return _buildRow(todo);
        });
  }
}

class UpcomingTodoTab extends StatefulWidget {
  @override
  UpcomingTodoTabState createState() => UpcomingTodoTabState();

  const UpcomingTodoTab();
}

class TodoCalendarTabState extends State<TodoCalendarTab> {
  @override
  Widget build(BuildContext context) {
    final todoListModel = Provider.of<TodoList>(context);
    final todoMap = todoListModel.getTodoListByActivities();
    return TodoWeekly(todoMap: todoMap);
  }
}

class TodoCalendarTab extends StatefulWidget {
  @override
  TodoCalendarTabState createState() => TodoCalendarTabState();

  const TodoCalendarTab();
}

class TodoSliderTabState extends State<TodoSliderTab> {
  CarouselSlider _carouselSlider;
  int _current = 0;

  List<T> map<T>(List list, Function handler) {
    List<T> result = [];
    for (var i = 0; i < list.length; i++) {
      result.add(handler(i, list[i]));
    }
    return result;
  }

  @override
  Widget build(BuildContext context) {
    final todoListModel = Provider.of<TodoList>(context);
    final todoList = todoListModel.getAllTodos();
    return Container(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: <Widget>[
          _carouselSlider = CarouselSlider(
            height: 550.0,
            initialPage: 0,
            enlargeCenterPage: true,
            autoPlay: true,
            reverse: false,
            enableInfiniteScroll: true,
            autoPlayInterval: Duration(seconds: 2),
            autoPlayAnimationDuration: Duration(milliseconds: 2000),
            pauseAutoPlayOnTouch: Duration(seconds: 10),
            scrollDirection: Axis.horizontal,
            onPageChanged: (index) {
              setState(() {
                _current = index;
              });
            },
            items: todoList.map((todo) {
              Random random = new Random();
              final int index = random.nextInt(17);
              final image = imageList[index];
              return Builder(
                builder: (BuildContext context) {
                  return Container(
                      width: MediaQuery.of(context).size.width,
                      margin: EdgeInsets.symmetric(horizontal: 5.0),
                      decoration: new BoxDecoration(
                        image: new DecorationImage(
                          image: image,
                          fit: BoxFit.cover,
                        ),
                      ),
                      child: new Stack(
                        children: <Widget>[
                          new Positioned(
                              left: 10.0,
                              right: 10.0,
                              bottom: 25.0,
                              child: TodoCard(todo: todo)),
                        ],
                      ));
                },
              );
            }).toList(),
          ),
          SizedBox(
            height: 10,
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: map<Widget>(todoList, (index, url) {
              return Container(
                width: 10.0,
                height: 10.0,
                margin: EdgeInsets.symmetric(vertical: 10.0, horizontal: 2.0),
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: _current == index ? Colors.lightBlue : Colors.grey,
                ),
              );
            }),
          ),
          SizedBox(
            height: 10.0,
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              OutlineButton(
                onPressed: goToPrevious,
                child: Text("<"),
              ),
              OutlineButton(
                onPressed: goToNext,
                child: Text(">"),
              ),
            ],
          ),
        ],
      ),
    );
  }

  goToPrevious() {
    _carouselSlider.previousPage(
        duration: Duration(milliseconds: 300), curve: Curves.ease);
  }

  goToNext() {
    _carouselSlider.nextPage(
        duration: Duration(milliseconds: 300), curve: Curves.decelerate);
  }
}

class TodoSliderTab extends StatefulWidget {
  @override
  TodoSliderTabState createState() => TodoSliderTabState();

  const TodoSliderTab();
}
