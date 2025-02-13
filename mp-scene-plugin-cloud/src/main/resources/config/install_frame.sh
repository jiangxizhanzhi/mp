mkdir -p /home/apps
cd /home/apps
curl -o frame.tar.gz ${frame_install_resource_url}
tar -zxvf frame.tar.gz
cd /home/apps/frame
docker-compose up -d