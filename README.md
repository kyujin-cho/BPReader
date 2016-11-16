# Boarding Pass Reader

An Android barcode reader app for boarding pass barcode(PDF417 type barcode).

A barcode printed on boarding pass has a bunch of datas [based on IATA Bar coded boarding pass rule](http://www.iata.org/whatwedo/stb/Pages/bcbp.aspx)

This android application parses data from raw text.


## Feature
- Reads boarding pass barcode
- Parses text input from barcode to readable output
- Makes PDF417 Barcode from input text

## Dependency
- [Jsoup](https://jsoup.org)
- [Zxing-android](https://github.com/zxing/zxing)

## How To 
- In order to install & run this project, you need to :
	1. Install Android Studio
	2. Install JDK
	3. Have a working Android OS Target Device (SDK min. ver 19, Does not recommend using AVD as a target device due to camera feature)
1. Clone this project 
2. Compile project & run

## TODO
- Convert IATA airport code (from boarding pass) to full airport name

## License
- Based on GPL 3.0 License