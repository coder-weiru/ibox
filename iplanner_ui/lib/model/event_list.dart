import 'dart:convert';
import 'dart:core';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart' show rootBundle;
import 'package:http/http.dart' as http;

import './user.dart';

class EventList {
  final List<Event> _events = List<Event>();
  final Set<Event> _savedEvents = Set<Event>();

  EventList() {
    loadEvents();
  }

  Set<Event> getSavedEvent() {
    return _savedEvents;
  }

  void addSavedEvent(Event event) {
    _savedEvents.add(event);
  }

  List<Event> getAllEvents() {
    return _events;
  }

  Event getEventByPosition(int index) {
    if (index < _events.length) {
      return _events.elementAt(index);
    } else {
      return null;
    }
  }

  Map<String, List<Event>> getEventListByActivities() {
    final Map<String, List<Event>> map = new Map();
    _events.forEach((event) {
      final activity = event.activity;
      map.putIfAbsent(activity, () => new List<Event>());
      List<Event> list = map[activity];
      list.add(event);
    });

    return map;
  }

  void loadEvents() async {
    var jsonStr = await rootBundle.loadString("assets/data/events.json");
    _events.addAll(await compute(parseEvents, jsonStr));
  }

  void fetchEvents() async {
    final response =
        await http.Client().get('https://jsonplaceholder.typicode.com/photos');

    // Use the compute function to run parseEvents in a separate isolate.
    _events.addAll(await compute(parseEvents, response.body));
  }
}

// Top-level functions
List<Event> parseEvents(String responseBody) {
  final parsed = json.decode(responseBody).cast<Map<String, dynamic>>();
  return parsed.map<Event>((json) => Event.fromJson(json)).toList();
}

@immutable
class Event {
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

  Event(
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

  factory Event.fromJson(Map<String, dynamic> json) {
    Set<String> recurrence = new Set();
    var iter = json['recurrence'].cast<List>().iterator;
    while (iter.moveNext()) {
      recurrence.add(iter.current);
    }
    return Event(
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
      other is Event &&
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
      'Event($id, $summary, $description, $activity, $creator, $start, $end, $created, $updated, $location, $recurrence, $status)';
}
