#!/bin/bash

# Elasticsearch Jaso Analyzer 릴리스 스크립트
# 사용법: ./release.sh [버전]
# 예: ./release.sh 9.0.3

VERSION=${1:-"9.0.3"}
PLUGIN_NAME="jaso-analyzer-plugin"

echo "🚀 Elasticsearch Jaso Analyzer v${VERSION} 릴리스 시작..."

# 0. Git 상태 확인 및 커밋
echo "📝 Git 상태 확인 중..."
if [ -n "$(git status --porcelain)" ]; then
    echo "📦 변경사항을 커밋 중..."
    git add .gitignore README.md build.gradle docker/ gradle/ src/ .github/ release.sh
    git commit -m "Update to Elasticsearch ${VERSION} with Java 21+ support

- Upgrade to Elasticsearch ${VERSION}
- Support Java 21+ (tested with Java 23)
- Update API compatibility for ES 9.x
- Add GitHub Actions workflow
- Improve .gitignore and documentation"
    
    if [ $? -eq 0 ]; then
        echo "✅ 변경사항 커밋 완료"
    else
        echo "❌ 커밋 실패!"
        exit 1
    fi
else
    echo "✅ 커밋할 변경사항 없음"
fi

# 1. 빌드
echo "📦 플러그인 빌드 중..."
./gradlew clean build buildPluginZip

if [ $? -ne 0 ]; then
    echo "❌ 빌드 실패!"
    exit 1
fi

# 2. 빌드 파일 확인
PLUGIN_FILE="build/distributions/${PLUGIN_NAME}-${VERSION}-plugin.zip"
if [ ! -f "$PLUGIN_FILE" ]; then
    echo "❌ 플러그인 파일을 찾을 수 없습니다: $PLUGIN_FILE"
    exit 1
fi

echo "✅ 플러그인 빌드 완료: $PLUGIN_FILE"

# 3. Git 태그 생성
echo "🏷️  Git 태그 생성 중..."
git tag -a "v${VERSION}" -m "Release version ${VERSION}"

if [ $? -eq 0 ]; then
    echo "✅ 태그 v${VERSION} 생성 완료"
    echo "💫 GitHub에 푸시하려면 다음 명령어를 실행하세요:"
    echo "   git push origin v${VERSION}"
    echo ""
    echo "📋 릴리스 노트:"
    echo "   - Elasticsearch ${VERSION} 지원"
    echo "   - Java 21+ 호환"
    echo "   - 한글 자소 분석 및 자동완성 지원"
else
    echo "⚠️  태그가 이미 존재합니다. 기존 태그를 덮어쓰려면:"
    echo "   git tag -d v${VERSION}"
    echo "   git push origin :refs/tags/v${VERSION}"
    echo "   그 후 이 스크립트를 다시 실행하세요."
fi

echo ""
echo "🎉 릴리스 준비 완료!"
echo "📁 플러그인 파일: $PLUGIN_FILE"
echo "📏 파일 크기: $(du -h "$PLUGIN_FILE" | cut -f1)"
