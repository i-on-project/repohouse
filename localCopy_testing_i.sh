!/bin/bash
ClassroomName=testing_i
mkdir classcode
cd classcode
mkdir $ClassroomName
cd $ClassroomName
declare -A repos
repos['testing_i-vvv_vvv-1']='https://github.com/test-project-isel/testing_i-vvv_vvv-1'
for repoKey in ${!repos[@]}
do
if [ -d $repoKey ];then
cd $repoKey
git pull
cd ..
else
git clone ${repos[$repoKey]}
fi
done
