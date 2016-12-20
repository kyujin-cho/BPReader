# Boarding Pass Reader
An Android barcode reader app to manage boarding pass.

A barcode printed on boarding pass has a bunch of datas based on [IATA Bar-coded boarding pass rule](https://www.iata.org/whatwedo/stb/Documents/BCBP-Implementation-Guide-5th-Edition-June-2016.pdf).

This android application parses data from raw text from those barcodes.

This application uses icon from [Google's material design](https://material.io/icons/ )(Apache 2.0 License)
## Introduction

### Base Language
This Project uses XML to design UI, and Java as a main language.

### Team Member
- [Kyujin Cho](http://thy2134.github.io)(Main Programmer / Designer)
- [Jaehyeon Ahn](http://IIru.github.io)(Designer)
- [Hanhyeok Hwang](http://Triplehwang.github.io)(Project Maintainer)

### Goal
Our goal is to create a well-formed Android application to manage the airplane trip for those who like to travel, or works internationally.

We have plan to expand this application as a total trip managing application.

## Feature
- Reads text inside boarding pass barcode
- Parses text input from barcode to readable output
- Makes PDF417 Barcode from input text

## Screenshots
![Screenshot 1](https://github.com/thy2134/BPReader/raw/master/Screenshots/Screenshot_20161220-174802.png)
![Screenshot 2](https://github.com/thy2134/BPReader/raw/master/Screenshots/Screenshot_20161220-174814.png)
![Screenshot 3](https://github.com/thy2134/BPReader/raw/master/Screenshots/Screenshot_20161220-174901.png)

## Library
- [Jsoup](https://jsoup.org)
	- Connects to internet and downloads newest Airline / Airport DB File
- [Zxing-android](https://github.com/zxing/zxing)
	- Scans PDF417 type barcode by camera

## How To
- In order to install & run this project, you need to :
	1. Install Android Studio
	2. Install JDK
	3. Have a working Android OS Target Device (SDK min. ver 19, Does not recommend using AVD as a target device due to camera feature)
1. Clone this project
2. Compile project & run

## What to do now?
- Airplane boarding time Alarm feature
- Automatically update Database periodically

## License
- This project is based on GPL 3.0 License
