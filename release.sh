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

# 1. 테스트 실행
echo "🧪 단위 테스트 실행 중..."
./gradlew clean test

if [ $? -ne 0 ]; then
    echo "❌ 테스트 실패! 릴리스를 중단합니다."
    echo "💡 테스트 리포트: build/reports/tests/test/index.html"
    exit 1
fi

# 테스트 결과 요약 표시
TEST_REPORT_XML="build/test-results/test"
if [ -d "$TEST_REPORT_XML" ]; then
    TOTAL_TESTS=$(find "$TEST_REPORT_XML" -name "TEST-*.xml" -exec grep -h "tests=" {} \; | sed 's/.*tests="\([0-9]*\)".*/\1/' | awk '{sum+=$1} END {print sum}')
    FAILED_TESTS=$(find "$TEST_REPORT_XML" -name "TEST-*.xml" -exec grep -h "failures=" {} \; | sed 's/.*failures="\([0-9]*\)".*/\1/' | awk '{sum+=$1} END {print sum}')
    ERROR_TESTS=$(find "$TEST_REPORT_XML" -name "TEST-*.xml" -exec grep -h "errors=" {} \; | sed 's/.*errors="\([0-9]*\)".*/\1/' | awk '{sum+=$1} END {print sum}')
    
    echo "✅ 테스트 완료!"
    echo "   📊 총 테스트: ${TOTAL_TESTS:-0}개"
    echo "   ✅ 성공: $((${TOTAL_TESTS:-0} - ${FAILED_TESTS:-0} - ${ERROR_TESTS:-0}))개"
    echo "   ❌ 실패: ${FAILED_TESTS:-0}개"
    echo "   🚫 에러: ${ERROR_TESTS:-0}개"
    
    if [ "${FAILED_TESTS:-0}" -gt 0 ] || [ "${ERROR_TESTS:-0}" -gt 0 ]; then
        echo "❌ 테스트에 실패하거나 에러가 있습니다!"
        echo "💡 상세 리포트: build/reports/tests/test/index.html"
        exit 1
    fi
fi

# 2. 빌드
echo "📦 플러그인 빌드 중..."
./gradlew buildPluginZip

if [ $? -ne 0 ]; then
    echo "❌ 빌드 실패!"
    exit 1
fi

# 3. 빌드 파일 확인
PLUGIN_FILE="build/distributions/${PLUGIN_NAME}-${VERSION}-plugin.zip"
if [ ! -f "$PLUGIN_FILE" ]; then
    echo "❌ 플러그인 파일을 찾을 수 없습니다: $PLUGIN_FILE"
    exit 1
fi

echo "✅ 플러그인 빌드 완료: $PLUGIN_FILE"

# 4. Git 태그 생성
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

# 릴리스 요약 표시
echo ""
echo "📋 릴리스 요약:"
echo "   🏷️  버전: v${VERSION}"
echo "   🧪 테스트: ${TOTAL_TESTS:-0}개 모두 통과"
echo "   📦 플러그인: $(basename "$PLUGIN_FILE")"
echo "   🎯 대상: Elasticsearch ${VERSION}"
echo "   ☕ Java: 21+ 호환"

# 다음 단계 안내
echo ""
echo "🚀 다음 단계:"
echo "   1. GitHub에 푸시: git push origin main"
echo "   2. 태그 푸시: git push origin v${VERSION}"
echo "   3. GitHub Releases에서 릴리스 생성"
echo "   4. 플러그인 설치: elasticsearch-plugin install https://github.com/USERNAME/elasticsearch-jaso-analyzer/releases/download/v${VERSION}/$(basename "$PLUGIN_FILE")"
