import 'package:flutter/material.dart';

// *Note*: when APP_VERSION is changed, remember to also update pubspec.yaml.
const APP_VERSION = 'v0.0.1';
const APP_NAME = 'i-Planner';
const APP_DESCRIPTION = 'An app helps manage schedule and tasks.'
    '\n\nDeveloped by Chris Ru.';

const HEADER_HEIGHT = 128.0;
const MENU_EVENTS = 'My Todos';
const MENU_ACTIVITIES = 'My Activities';

final Widget splash = Image.asset(
  'assets/images/home-office.jpg',
  fit: BoxFit.cover,
);

final AssetImage imageAtom = AssetImage('assets/images/atom.jpg');

final AssetImage imageBackToSchool =
    AssetImage('assets/images/back-to-school.jpg');

final AssetImage imageBrain = AssetImage('assets/images/brain.jpg');

final AssetImage imageBrainstorm = AssetImage('assets/images/brainstorm.jpg');

final AssetImage imageBulbs = AssetImage('assets/images/bulbs.jpg');

final AssetImage imageExhausted = AssetImage('assets/images/exhausted.jpg');

final AssetImage imageLearn = AssetImage('assets/images/learn.jpg');

final AssetImage imageMan_1 = AssetImage('assets/images/man_1.jpg');

final AssetImage imageMan_2 = AssetImage('assets/images/man_2.jpg');

final AssetImage imageOffice = AssetImage('assets/images/office.jpg');

final AssetImage imagePaper = AssetImage('assets/images/paper.jpg');

final AssetImage imageQuestions = AssetImage('assets/images/questions.jpg');

final AssetImage imageReading = AssetImage('assets/images/reading.jpg');

final AssetImage imageSchool_1 = AssetImage('assets/images/school_1.jpg');

final AssetImage imageSchool_2 = AssetImage('assets/images/school_2.jpg');

final AssetImage imageSchool_3 = AssetImage('assets/images/school_3.jpg');

final AssetImage imageScience = AssetImage('assets/images/science.jpg');

final List<AssetImage> imageList = [
  imageAtom,
  imageBackToSchool,
  imageBrain,
  imageBrainstorm,
  imageBulbs,
  imageExhausted,
  imageLearn,
  imageMan_1,
  imageMan_2,
  imageOffice,
  imagePaper,
  imageQuestions,
  imageReading,
  imageSchool_1,
  imageSchool_2,
  imageSchool_3,
  imageScience
];
