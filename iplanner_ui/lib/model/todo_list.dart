import 'dart:convert';
import 'dart:core';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart' show rootBundle;
import 'package:http/http.dart' as http;

import './user.dart';

class TodoList {
  final List<Todo> _todos = List<Todo>();
  final Set<Todo> _savedTodos = Set<Todo>();

  TodoList() {
    loadTodos();
  }

  Set<Todo> getSavedTodo() {
    return _savedTodos;
  }

  void addSavedTodo(Todo todo) {
    _savedTodos.add(todo);
  }

  List<Todo> getAllTodos() {
    return _todos;
  }

  Todo getTodoByPosition(int index) {
    if (index < _todos.length) {
      return _todos.elementAt(index);
    } else {
      return null;
    }
  }

  Map<String, List<Todo>> getTodoListByActivities() {
    final Map<String, List<Todo>> map = new Map();
    _todos.forEach((todo) {
      final activity = todo.activity;
      map.putIfAbsent(activity, () => new List<Todo>());
      List<Todo> list = map[activity];
      list.add(todo);
    });

    return map;
  }

  void loadTodos() async {
    var jsonStr = await rootBundle.loadString("assets/data/todos.json");
    _todos.addAll(await compute(parseTodoList, jsonStr));
  }

  void fetchTodos() async {
    final response =
        await http.Client().get('https://jsonplaceholder.typicode.com/photos');

    // Use the compute function to run parseTodos in a separate isolate.
    _todos.addAll(await compute(parseTodoList, response.body));
  }
}

// Top-level functions
List<Todo> parseTodoList(String responseBody) {
  final parsed = json.decode(responseBody).cast<Map<String, dynamic>>();
  return parsed.map<Todo>((json) => Todo.fromJson(json)).toList();
}

@immutable
class Todo {
  final String id;
  final String summary;
  final String description;
  final String activity;
  final User creator;
  final DateTime start;
  final DateTime end;
  final DateTime created;
  final DateTime updated;
  final String status;
  final String location;
  final Set<String> recurrence;

  Todo(
      {this.id,
      this.summary,
      this.description,
      this.activity,
      this.creator,
      this.start,
      this.end,
      this.created,
      this.updated,
      this.location,
      this.recurrence,
      status})
      : status = status ?? "OPEN";

  factory Todo.fromJson(Map<String, dynamic> json) {
    Set<String> recurrence = new Set();
    var iter = json['recurrence'].cast<List>().iterator;
    while (iter.moveNext()) {
      recurrence.add(iter.current);
    }
    return Todo(
      id: json['id'] as String,
      summary: json['summary'] as String,
      description: json['description'] as String,
      activity: json['activity'] as String,
      creator: User.fromJson(json['creator']),
      start: DateTime.parse(json['start']),
      end: DateTime.parse(json['end']),
      created: DateTime.parse(json['created']),
      updated: DateTime.parse(json['updated']),
      location: json['location'] as String,
      recurrence: recurrence,
      status: json['status'],
    );
  }

  @override
  int get hashCode => id.hashCode;

  @override
  bool operator ==(Object other) =>
      other is Todo &&
      other.id == id &&
      other.summary == summary &&
      other.description == description &&
      other.activity == activity &&
      other.creator == creator &&
      other.start == start &&
      other.end == end &&
      other.created == created &&
      other.updated == updated &&
      other.location == location &&
      other.recurrence == recurrence &&
      other.status == status;

  @override
  String toString() =>
      'Todo($id, $summary, $description, $activity, $creator, $start, $end, $created, $updated, $location, $recurrence, $status)';
}
