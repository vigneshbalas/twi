**A long time ago in a galaxy far, far away....**

  <img align="left" width="100" alt="Yoda Star-Wars" src="https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Yoda_Star-Wars.jpg/256px-Yoda_Star-Wars.jpg?20231019100020">
 A lone jedi knight had to travel far and wide across the galaxy, even with the help of the force, he found it hard to keep track of different timezones....

Help me master, he knelt beside his grand master **Yoda**. Help me to quickly navigate the times... I need to convert dates and time faster...

Master Yoda, slowly opened his eyes, "**Time what is?** You ask".Hmm? Hmm.. I will help.. For my ally is the Force, and a powerful ally it is......

# About twi

A simple CLI tool to be able to convert time and date, including timezone conversions. It uses natural language processing to parse time and date components from english text and convert using the awesome joda-time library.

I created this tool because I am more comfortable with CLI and I could not find a open source application that fit my needs.

The CLI is built using picocli library.

# NLP Parser
There are quite a number of NLP parsers available for java like **Apache OpenNLP**, **Stanford NLP**, etc but I preferred not to use any of them due to a couple of reasons:
  - Most of the available libraries are not supporting some of the grammars I frequently use.
  - Some of these libraries are under a more restrictive open source license and I wanted this tool to be under MIT License.

For these reasons, I created a NLP parser from scratch, for date-time processing. its kept simple intentionally and only supports English as of now. Test folder contains a test class with all supported formats as of now.
# Compatibility
The tool is developed in Java so should work in most platforms. I tested in Windows 11 though. 
I developed using JDK 21, but it should work in JDKs 8 or more
# What twi is
Its a lightweight tool that can be used to convert date times. The code is oriented towards simplicity over performance. Feel free to review the code, modify as you wish for your needs.
# What twi is NOT
- This software is provided AS IS without any warranty.
- It is not tested for Production use.
# Acknowledgements
- I would like to thank the following Authors/Websites for making the data set available for use:
  - Country Codes and Country Name reference data as JSON from - https://country.io/data/
  - Country-Timezone mapping as JSON from - https://stackoverflow.com/a/40160169
- Yoda's Image - <a href="https://commons.wikimedia.org/wiki/File:Yoda_Star-Wars.jpg">photographied by Supremerian1988</a>, Public domain, via Wikimedia Commons

  






