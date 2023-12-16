FROM ubuntu:jammy

ENV OS_LOCALE="en_US.UTF-8"
RUN apt-get update && apt-get install -y locales && locale-gen ${OS_LOCALE}

ENV LANG=${OS_LOCALE} \
    LANGUAGE=${OS_LOCALE} \
    LC_ALL=${OS_LOCALE} \
    DEBIAN_FRONTEND=noninteractive

ENV APACHE_CONF_DIR=/etc/apache2 \
    PHP_CONF_DIR=/etc/php/8.1 \
    PHP_DATA_DIR=/var/lib/php

COPY entrypoint.sh /sbin/entrypoint.sh

# Export the Android SDK path
ENV ANDROID_HOME=/usr/lib/android-sdk
ENV ANDROID_SDK_ROOT=/usr/lib/android-sdk
ENV APKTOOL_VERSION="2.6.0"
ENV PATH="${PATH}:${ANDROID_HOME}/tools/bin:/usr/local/bin"

RUN	BUILD_DEPS='software-properties-common' \
    && dpkg-reconfigure locales \
	&& apt-get install --no-install-recommends -y $BUILD_DEPS \
	&& apt-get update \
	&& apt-get install -y curl apache2 libapache2-mod-php8.1 php8.1-cli php8.1-dev php8.1-common php8.1-mysql php8.1-zip php8.1-gd php8.1-mbstring php8.1-curl php8.1-xml php8.1-bcmath \
    && apt-get install -y libreadline-dev \
    && apt-get install -y build-essential \
    # Apache settings
    && cp /dev/null ${APACHE_CONF_DIR}/conf-available/other-vhosts-access-log.conf \
    && rm ${APACHE_CONF_DIR}/sites-enabled/000-default.conf ${APACHE_CONF_DIR}/sites-available/000-default.conf \
    && a2enmod rewrite php8.1 \
	# Install composer
	&& curl -sS https://getcomposer.org/installer | php -- --version=1.8.4 --install-dir=/usr/local/bin --filename=composer  \
	# Cleaning
	&& ln -sf /dev/stdout /var/log/apache2/access.log \
	&& ln -sf /dev/stderr /var/log/apache2/error.log \
	&& chmod 755 /sbin/entrypoint.sh \
	&& chown www-data:www-data ${PHP_DATA_DIR} -Rf \
	# Install latest JDK
    && apt-get install -y default-jdk default-jre zipalign unzip android-sdk wget imagemagick libpq-dev python3.10 python3-pip

RUN apt-get install -y --no-install-recommends \
    		dpkg-dev \
    		gcc \
    		gnupg dirmngr \
    		libbluetooth-dev \
    		libbz2-dev \
    		libc6-dev \
    		libexpat1-dev \
    		libffi-dev \
    		libgdbm-dev \
    		liblzma-dev \
    		libncursesw5-dev \
    		libsqlite3-dev \
    		libssl-dev \
    		make \
    		tk-dev \
    		uuid-dev \
    		wget \
    		xz-utils \
    		zlib1g-dev

# Install the needed tools.
RUN apt install --no-install-recommends -y openjdk-11-jdk-headless default-jre-headless openjdk-11-jre-headless jarwrapper unzip && \
    # Apktool.
    wget -q "https://raw.githubusercontent.com/iBotPeaches/Apktool/master/scripts/linux/apktool" \
    -O /usr/local/bin/apktool && chmod a+x /usr/local/bin/apktool && \
    wget -q "https://bitbucket.org/iBotPeaches/apktool/downloads/apktool_${APKTOOL_VERSION}.jar" \
    -O /usr/local/bin/apktool.jar && chmod a+x /usr/local/bin/apktool.jar && \
    # BundleDecompiler.
    wget -q "https://raw.githubusercontent.com/TamilanPeriyasamy/BundleDecompiler/master/build/libs/BundleDecompiler-0.0.2.jar" \
    -O /usr/local/bin/BundleDecompiler.jar && chmod a+x /usr/local/bin/BundleDecompiler.jar
    # Clean.
#    apt remove --purge -y wget unzip && \
#    apt autoremove --purge -y && apt clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* \


RUN export ANDROID_HOME=/usr/lib/android-sdk \
		&& echo $ANDROID_HOME \
 		&& export PATH=$PATH:$ANDROID_HOME/tools/bin \
		&& export PATH=$PATH:$ANDROID_HOME/platform-tools \
		&& export JAVA_OPTS='-XX:+IgnoreUnrecognizedVMOptions --add-modules java.se.ee' \
		&& /bin/bash -c "source ~/.bashrc"

RUN wget https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip \
 	&& unzip commandlinetools-linux-8512546_latest.zip -d $ANDROID_HOME/cmdline-tools \
	&& mv $ANDROID_HOME/cmdline-tools/cmdline-tools $ANDROID_HOME/cmdline-tools/tools

ENV PATH=":${ANDROID_HOME}/tools/bin:${PATH}"
ENV PATH=":${ANDROID_HOME}/cmdline-tools/tools/bin:${PATH}"
ENV PATH=":${ANDROID_HOME}/cmdline-tools/tools:${PATH}"

RUN yes | sdkmanager --licenses
RUN sdkmanager --install "build-tools;28.0.3"
RUN sdkmanager --install "platform-tools"
RUN sdkmanager --install "platforms;android-28"
RUN sdkmanager --install "platforms;android-30" 

RUN yes | sdkmanager --licenses
RUN apt-get install -y android-sdk-build-tools

RUN apt-get install -y apksigner
RUN update-alternatives --config javac
RUN update-alternatives --config java

COPY ./configs/apache2.conf ${APACHE_CONF_DIR}/apache2.conf
COPY ./configs/app.conf ${APACHE_CONF_DIR}/sites-enabled/app.conf
COPY ./configs/php.ini  ${PHP_CONF_DIR}/apache2/conf.d/custom.ini

RUN mkdir /var/www/app/ && \
    mkdir /var/www/Obfuscapk/ &&  \
    mkdir /var/www/source/ &&  \
    mkdir /var/www/app/../build/ && \
    mkdir /var/www/app/../build/tmpfile/ &&  \
    mkdir /var/www/app/../build/buildFile/ &&  \
    mkdir /var/www/app/../build/buildFile/63a9f0ea7bb98050796b649e85481845/ &&  \
    mkdir /var/www/app/../build/tmpfile/63a9f0ea7bb98050796b649e85481845/

RUN chown www-data:www-data ${PHP_DATA_DIR} -Rf

WORKDIR /var/www/
WORKDIR /var/www/app/
WORKDIR /var/www/app/../build/
WORKDIR /var/www/app/../build/tmpfile/
WORKDIR /var/www/app/../build/tmpfile/63a9f0ea7bb98050796b649e85481845/
WORKDIR /var/www/app/../build/buildFile/
WORKDIR /var/www/app/../build/buildFile/63a9f0ea7bb98050796b649e85481845/
WORKDIR /var/www/Obfuscapk/
WORKDIR /var/www/source/
WORKDIR /var/www/.gradle
WORKDIR /var/www/.android
WORKDIR /var/www/.cache
WORKDIR /var/www/.local
WORKDIR /usr/lib/android-sdk
WORKDIR /usr/lib/android-sdk/build-tools/

COPY ./app /var/www/app/
COPY ./Obfuscapk /var/www/Obfuscapk
COPY ./source /var/www/source
COPY ./build /var/www/build

RUN chown www-data:www-data /var/www/app/ -Rf  \
&& chown www-data:www-data /var/www/ -Rf \
&& chown www-data:www-data /var/www/app/../build/ -Rf \
&& chown www-data:www-data /var/www/app/../build/tmpfile/ -Rf \
&& chown www-data:www-data /var/www/app/../build/tmpfile/63a9f0ea7bb98050796b649e85481845/ -Rf \
&& chown www-data:www-data /var/www/app/../build/buildFile/ -Rf \
&& chown www-data:www-data /var/www/app/../build/buildFile/63a9f0ea7bb98050796b649e85481845/ -Rf \
&& chown www-data:www-data /var/www/Obfuscapk -Rf \
&& chown www-data:www-data /var/www/source -Rf \
&& chown www-data:www-data /var/www/.gradle -Rf \
&& chown www-data:www-data /var/www/.android -Rf \
&& chown www-data:www-data /var/www/.cache -Rf \
&& chown www-data:www-data /var/www/.local -Rf \
&& chown www-data:www-data /usr/lib/android-sdk -Rf \
&& chown www-data:www-data /usr/lib/android-sdk/build-tools/ -Rf

RUN chmod -R 777 /var/www/app/../build/buildFile/
RUN chmod -R 777 /var/www/app/../build/tmpfile/
RUN chmod -R 777 /var/www/app/../build/buildFile/63a9f0ea7bb98050796b649e85481845/
RUN chmod -R 777 /var/www/app/../build/tmpfile/63a9f0ea7bb98050796b649e85481845/

RUN python3 --version
RUN pip3 install --upgrade setuptools
RUN pip3 install multidict
RUN pip3 install typing_extensions
RUN pip3 install attr
RUN pip3 install yarl
RUN pip3 install async_timeout
RUN pip3 install idna_ssl
RUN python3 -m pip install --no-cache-dir -r /var/www/Obfuscapk/src/requirements.txt

EXPOSE 80

# By default, simply start apache.
CMD ["/sbin/entrypoint.sh"]