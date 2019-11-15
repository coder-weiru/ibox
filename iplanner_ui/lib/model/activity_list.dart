import 'dart:convert';
import 'dart:core';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart' show rootBundle;
import 'package:http/http.dart' as http;

import './attributes.dart';
import './user.dart';

class ActivityList {
  final List<Activity> _activities = List<Activity>();
  final Set<Activity> _savedActivities = Set<Activity>();

  ActivityList() {
    loadActivities();
  }

  List<Activity> getAllActivities() {
    return _activities;
  }

  Set<Activity> getSavedActivity() {
    return _savedActivities;
  }

  void addSavedActivity(Activity activity) {
    _savedActivities.add(activity);
  }

  Activity getActivityByPosition(int index) {
    if (index < _activities.length) {
      return _activities.elementAt(index);
    } else {
      return null;
    }
  }

  void loadActivities() async {
    var jsonStr = await rootBundle.loadString("assets/data/activities.json");
    _activities.addAll(await compute(parseActivities, jsonStr));
  }

  void fetchActivities() async {
    final response =
        await http.Client().get('https://jsonplaceholder.typicode.com/photos');

    // Use the compute function to run parseActivities in a separate isolate.
    _activities.addAll(await compute(parseActivities, response.body));
  }
}

// Top-level functions
List<Activity> parseActivities(String responseBody) {
  final parsed = json.decode(responseBody).cast<Map<String, dynamic>>();
  return parsed.map<Activity>((json) => Activity.fromJson(json)).toList();
}

@immutable
class Activity {
  final String id;
  final String title;
  final String description;
  final String activityType;
  final User creator;
  final DateTime created;
  final DateTime updated;
  final String _status;
  final TagAttribute tags;
  final EventAttribute eventInfo;
  final LocationAttribute locationInfo;
  final TimelineAttribute timeline;

  Activity(
      {this.id,
      this.title,
      this.description,
      this.activityType,
      this.creator,
      this.created,
      this.updated,
      this.tags,
      this.eventInfo,
      this.locationInfo,
      this.timeline,
      status})
      : _status = status ?? "ACTIVE";

  factory Activity.fromJson(Map<String, dynamic> json) {
    return Activity(
      id: json['id'] as String,
      title: json['title'] as String,
      description: json['description'] as String,
      activityType: json['activityType'] as String,
      creator: User.fromJson(json['creator']),
      created: DateTime.parse(json['created']),
      updated: DateTime.parse(json['updated']),
      status: json['status'],
      tags:
          json.containsKey('tags') ? TagAttribute.fromJson(json['tags']) : null,
      eventInfo: json.containsKey('eventInfo')
          ? EventAttribute.fromJson(json['eventInfo'])
          : null,
      locationInfo: json.containsKey('locationInfo')
          ? LocationAttribute.fromJson(json['locationInfo'])
          : null,
      timeline: json.containsKey('timeline')
          ? TimelineAttribute.fromJson(json['timeline'])
          : null,
    );
  }

  @override
  int get hashCode => id.hashCode;

  @override
  bool operator ==(Object other) =>
      other is Activity &&
      other.id == id &&
      other.title == title &&
      other.description == description &&
      other.activityType == activityType &&
      other.creator == creator &&
      other.created == created &&
      other.updated == updated &&
      other._status == _status;

  @override
  String toString() =>
      'Activity($id, $title, $description, $activityType, $creator, $created, $updated, $_status $tags, $eventInfo, $locationInfo, $timeline)';
}
