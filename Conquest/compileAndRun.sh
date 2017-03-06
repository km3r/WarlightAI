rm -rf bin
echo $1
find . -type f \( -iname "*.java" \) > sources.txt
mkdir bin
javac -d bin @sources.txt
rm sources.txt
mkdir -p bin/conquest/view/resources/images
cp src/conquest/view/resources/images/* bin/conquest/view/resources/images/
let bot1 = $1
let bot2 = $2
#if [!$1 && !$2]
#    read -p "Enter bot 1:" bot1
#    read -p "Enter bot 2:" bot2
#fi
# max number of rounds | bot command timeout | bot 1 init | bot 2 init | visualization | replay file
java -cp bin conquest.Conquest 300 5000 "internal:conquest.bot.BotStarter" "process:java -cp bin conquest.bot.BotStarter" true replay.log
