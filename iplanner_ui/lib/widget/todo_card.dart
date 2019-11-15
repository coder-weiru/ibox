import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:iplanner_ui/model/todo_list.dart';

import '../common/colors.dart';

class TodoCard extends StatelessWidget {
  final Todo _todo;

  const TodoCard({@required todo}) : _todo = todo;

  Todo get todo => this._todo;

  @override
  Widget build(BuildContext context) {
    final summary = todo.summary ?? "";
    final description = todo.description ?? "";
    final activity = todo.activity;
    final start = todo.start;
    final end = todo.end;
    final location = todo.location ?? "";

    return Container(
        width: MediaQuery.of(context).size.width,
        margin: EdgeInsets.symmetric(horizontal: 10.0),
        decoration: new BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(25.0),
        ),
        child: Card(
            color: getTodoColor(activity),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(25.0),
            ),
            margin: EdgeInsets.symmetric(horizontal: 10.0, vertical: 10.0),
            elevation: 10,
            child: Padding(
              padding: const EdgeInsets.only(
                top: 10.0,
                bottom: 10.0,
                left: 10.0,
                right: 10.0,
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: <Widget>[
                  Text(summary, style: Theme.of(context).textTheme.headline),
                  Text(description, style: Theme.of(context).textTheme.subhead),
                  new Divider(),
                  Row(children: <Widget>[
                    Text(new DateFormat("EEEE, MMMM d").format(start),
                        style: Theme.of(context).textTheme.title),
                  ]),
                  Row(children: <Widget>[
                    Text(new DateFormat("HH:mm").format(start),
                        style: Theme.of(context).textTheme.subhead),
                    Text(" - ", style: Theme.of(context).textTheme.subhead),
                    Text(new DateFormat("HH:mm").format(end),
                        style: Theme.of(context).textTheme.subhead),
                  ]),
                  new Divider(),
                  Visibility(
                    visible: location != "",
                    child: Row(
                      children: <Widget>[
                        Icon(
                          Icons.star,
                        ),
                        Text(location,
                            style: Theme.of(context).textTheme.subhead),
                      ],
                    ),
                  ),
                ],
              ),
            )));
  }
}
