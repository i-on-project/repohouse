// @ts-check
import { test, expect, chromium } from '@playwright/test';
import * as fs from 'fs';

test('In menu page', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/student_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the auth tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await expect(page.url()).toBe('http://localhost:3000/menu');
});

test('Invite Modal', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/student_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the auth tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await expect(page.url()).toBe('http://localhost:3000/menu');
    await expect(page.getByRole('heading', { name: 'Welcome i-on-ClassCode-Student' })).toBeVisible();
    await page.getByRole('button', { name: 'Invite Code' }).click();
    await expect(page.getByLabel('InviteCode')).toBeVisible();
});

test('Course page', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/student_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the auth tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await expect(page.url()).toBe('http://localhost:3000/menu');
    await page.locator('div').filter({ hasText: /^ClassCode$/ }).nth(2).click();
    await expect(page.locator('h2')).toBeVisible();
});

test('Class page', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/student_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the auth tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await expect(page.url()).toBe('http://localhost:3000/menu');
    await page.locator('div').filter({ hasText: /^ClassCode$/ }).nth(2).click();
    await page.locator('h5').click();
    await expect(page.getByRole('heading', { name: 'ClassCode' })).toBeVisible()
    await expect(page.getByText('i-on-ClassCode-Student - 1')).toBeVisible();
});

test('Assignment page', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/student_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the auth tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await expect(page.url()).toBe('http://localhost:3000/menu');
    await page.locator('div').filter({ hasText: /^ClassCode$/ }).nth(2).click();
    await page.locator('h5').click();
    await page.getByRole('link', { name: 'Assignment title - Assignment description' }).click();
    await expect(page.getByRole('heading', { name: 'Assignment title' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Assignment description' })).toBeVisible();
});

test('Delivery page', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/student_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the auth tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await expect(page.url()).toBe('http://localhost:3000/menu');
    await page.locator('div').filter({ hasText: /^ClassCode$/ }).nth(2).click();
    await page.locator('h5').click();
    await page.getByRole('link', { name: 'Assignment title - Assignment description' }).click();
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.getByRole('heading', { name: 'Delivery #1' }).click();
    await expect(page.getByRole('heading', { name: 'Delivery # 1' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'tag' })).toBeVisible();
});

test('Team page', async ({ page }) => {
    const cookies = fs.readFileSync('./tests/student_cookies.json', 'utf8')
    if (cookies == null) {
        console.log('No cookies found - run the auth tests first')
        test.fail()
    }
    const deserializedCookies = JSON.parse(cookies)
    await page.context().addCookies(deserializedCookies);
    await page.goto('http://localhost:3000/');
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await expect(page.url()).toBe('http://localhost:3000/menu');
    await page.locator('div').filter({ hasText: /^ClassCode$/ }).nth(2).click();
    await page.locator('h5').click();
    await page.getByRole('link', { name: 'Assignment title - Assignment description' }).click();
    await page.waitForLoadState('load');
    await page.waitForTimeout(500);
    await page.getByRole('heading', { name: 'Team 1' }).click();
    await expect(page.getByRole('heading', { name: 'Team 1' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Repo 1' })).toBeVisible();
});
