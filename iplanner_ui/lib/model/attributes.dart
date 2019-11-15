import 'dart:core';

import 'package:flutter/material.dart';

@immutable
class Tag {
  final String value;
  final String argbValue;
  final bool primary;

  Tag({this.value, this.argbValue, this.primary});

  factory Tag.fromJson(Map<String, dynamic> json) {
    return Tag(
        value: json['value'] as String,
        argbValue: json['argbValue'] as String,
        primary: json['primary'] as bool);
  }

  @override
  int get hashCode => value.hashCode ^ argbValue.hashCode ^ primary.hashCode;

  @override
  bool operator ==(Object other) =>
      other is Tag &&
      other.value == value &&
      other.argbValue == argbValue &&
      other.primary == primary;

  @override
  String toString() => 'Tag($value, $argbValue, $primary)';
}

@immutable
class TagAttribute {
  final Set<Tag> _tags;

  TagAttribute({tags}) : _tags = tags ?? Set<Tag>();

  factory TagAttribute.fromJson(Map<String, dynamic> json) {
    var list = json['tags'] as List;
    Set<Tag> tagSet = list.map((value) => Tag.fromJson(value)).toSet();
    return TagAttribute(tags: tagSet);
  }

  Set<Tag> get tags {
    return _tags;
  }

  Tag getPrimaryTag() {
    return _tags.firstWhere((tag) => tag.primary == true, orElse: () => null);
  }
}

@immutable
class EventAttribute {
  final DateTime start;
  final DateTime end;
  final String frequency;
  final Set<String> recurrence;

  EventAttribute({this.start, this.end, this.frequency, this.recurrence});

  factory EventAttribute.fromJson(Map<String, dynamic> json) {
    Set<String> recurrenceSet = Set<String>();
    json['recurrence'].map((value) => recurrenceSet.add(value));
    return EventAttribute(
        start: DateTime.parse(json['start']),
        end: DateTime.parse(json['end']),
        frequency: json['frequency'] as String,
        recurrence: recurrenceSet);
  }
}

@immutable
class LocationAttribute {
  final String location;

  LocationAttribute({this.location});

  factory LocationAttribute.fromJson(Map<String, dynamic> json) {
    return LocationAttribute(location: json['location'] as String);
  }
}

@immutable
class TimelineAttribute {
  final DateTime startBy;
  final DateTime completeBy;

  TimelineAttribute({this.startBy, this.completeBy});

  factory TimelineAttribute.fromJson(Map<String, dynamic> json) {
    return TimelineAttribute(
        startBy: json.containsKey('startBy')
            ? null
            : DateTime.parse(json['startBy']),
        completeBy: json.containsKey('completeBy')
            ? null
            : DateTime.parse(json['completeBy']));
  }
}
