import 'dart:core';

import 'package:flutter/material.dart';

@immutable
class User {
  final String id;
  final String email;
  final String displayName;

  User({this.id, this.email, this.displayName});

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
        id: json['id'] as String,
        email: json['email'] as String,
        displayName: json['displayName'] as String);
  }

  @override
  int get hashCode => id.hashCode;

  @override
  bool operator ==(Object other) =>
      other is User &&
      other.id == id &&
      other.email == email &&
      other.displayName == displayName;

  @override
  String toString() => 'User($id, $email, $displayName)';
}
