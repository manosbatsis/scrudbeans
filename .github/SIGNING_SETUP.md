
1. List your keys to find the right ID:

```
gpg --list-secret-keys --keyid-format=short

```


2. Export ascii-armored key for in-memory use by Gradle:

```
gpg --export-secret-keys --armor AABBCCDD > AABBCCDD.asc

```

3. You now have the key as safe-to-transfer text:

```
-----BEGIN PGP PRIVATE KEY BLOCK-----

--== The key sequence itself ==--

-----END PGP PRIVATE KEY BLOCK-----
```

4. Add it as a secret named `ORG_GRADLE_PROJECT_SIGNINGKEY`,
along with the key id as  `ORG_GRADLE_PROJECT_SIGNINGKEYID`
and your key password as `ORG_GRADLE_PROJECT_SIGNINGPASSWORD`.

These are used in Github Actions from Gradle as follows:

```kotlin
signing {
	if (signingEnabled.get()) {
		sign(publishing.publications["mavenJava"])

		useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
	}
}
```

To be able to sign locally the armored key may be “escaped” to put
inside your local.properties (line-breaks transformed):

```properties
signingKey=-----BEGIN PGP PRIVATE KEY BLOCK-----\n\nescaped key\n-----END PGP PRIVATE KEY BLOCK-----\n
signingPassword=pass
ossrhUsername=user
ossrhPassword=pass
```
