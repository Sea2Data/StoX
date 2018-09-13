#generate ssh key
#setx HOME "C:/Users/aasmunds"
# ssh-keygen -t rsa -b 4096 -C "aasmunds@imr.no"
# 
# add .ssh/id_rsa.pub content into GitHub as new SSH key

#read -rsp $'Press any key to continue...' -n 1 key
export HOME="C:/Users/aasmunds"
cd c:/Projects/Git/RStox
git pull
cp C:/Projects/Git/StoX/stox/stox-core/target/stox.jar C:/Projects/Git/RStox/inst/java/stox.jar
git commit -a -m "Updated stox.jar"
git push
read -rsp $'Press any key to continue...' -n 1 key
