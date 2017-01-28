# sc2quiz
> Generate a simple Quiz mod for Starcraft II

Want to revise and play at the same time?
This program generates a Starcraft II mod (LOV) that will ask you to select an answer 
every 60 seconds within 3 possibilities. You earn 400 minerals if you are correct.
You can enter the questions and answers in an Excel file (see examples in xls folder).

### Prerequisites

You will need:
- Java 1.8 (however you can probably use a lower version by changing the gradle build file) 
- git

### Building

```shell
git clone https://github.com/khodev/sc2quiz.git
cd sc2quiz
gradlew build
```

Copy the file `build/libs/sc2quiz.jar` in the directory of your choice.

### Using

```shell
java -jar sc2quiz.jar -h
usage: java -jar sc2quiz.jar -<command> [...]
 -dn,--document-name <arg>                  set the document title of the
                                            mod
 -g,--generate <file.xls> <folder.SC2Mod>   Generate an sc2 quiz mod
 -h,--help                                  show this help
 -ld,--long-description <arg>               set the long description of
                                            the mod
 -sd,--short-description <arg>              set the short description of
                                            the mod
```

You can generate the example attached* `xls/czech_english.xlsx`:

```shell
java -jar sc2quiz.jar -g xls/czech_english.xlsx result -dn "Czech quiz!" -ld "Quiz mod with top 2000 Czech words"
```

Open the folder with SC2 editor and publish it to your account.

*Please note that this word list is not exactly great, if you have a better one don't hesitate to contact me!
   
### Contributing

I welcome any suggestions and comments.  

### Licensing

The code in this project is licensed under [MIT license](./LICENSE).
